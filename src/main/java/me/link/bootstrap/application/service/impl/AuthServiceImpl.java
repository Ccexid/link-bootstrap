package me.link.bootstrap.application.service.impl;

import me.link.bootstrap.application.service.AuthService;
import me.link.bootstrap.application.service.UserService;


import cn.hutool.crypto.digest.BCrypt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.link.bootstrap.interfaces.dto.response.vo.TokenResponseVO;
import me.link.bootstrap.infrastructure.persistence.po.UserPO;
import me.link.bootstrap.infrastructure.security.EmailCodeSendRateLimitService;
import me.link.bootstrap.infrastructure.security.EmailCodeService;
import me.link.bootstrap.infrastructure.security.HumanVerificationService;
import me.link.bootstrap.infrastructure.security.LoginAttemptService;
import me.link.bootstrap.infrastructure.security.SecurityTokenSession;
import me.link.bootstrap.infrastructure.security.SecurityTokenSessionService;
import me.link.bootstrap.interfaces.dto.request.auth.EmailLoginRequest;
import me.link.bootstrap.interfaces.dto.request.auth.LoginRequest;
import me.link.bootstrap.interfaces.dto.request.auth.SendEmailCodeRequest;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import me.link.bootstrap.shared.kernel.util.SecurityHelper;
import me.link.bootstrap.shared.kernel.valueobject.StatusEnum;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 认证应用服务,负责登录、登出的业务编排。
 * <p>
 * 账号密码登录流程:按 username 跨租户查询唯一用户 → BCrypt 校验密码 → 校验账号状态 →
 * 创建 Redis 不透明 Bearer Token 会话,供后续多租户隔离与权限判断使用。
 * </p>
 * <p>
 * <b>@TenantIgnore</b>:登录时尚未建立 Spring Security 认证上下文,
 * {@link me.link.bootstrap.shared.kernel.database.mybatis.LinkTenantLineHandler} 取不到 tenantId,
 * 默认行为会让查询 SQL 退化为 {@code tenant_id IS NULL} 而查不到数据。
 * 登录查询由用户应用服务的最小查询方法显式标注绕过租户拦截,认证成功后再将用户上下文写入 Token 会话。
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final LoginAttemptService loginAttemptService;
    private final EmailCodeService emailCodeService;
    private final HumanVerificationService humanVerificationService;
    private final EmailCodeSendRateLimitService emailCodeSendRateLimitService;
    private final SecurityTokenSessionService securityTokenSessionService;

    /**
     * 账号密码登录。
     * <p>
     * 错误响应均统一为业务异常,HTTP 状态码由 GlobalExceptionHandler 处理。
     * 登录成功后会将 tenantId、userType、isSuperAdmin 写入 Redis Token 会话,
     * 供 SecurityHelper / LinkTenantLineHandler 在后续请求中使用。
     * </p>
     * <p>
     * <b>@TenantIgnore 位置</b>:仅作用于 {@code UserService.findByUsernameForLogin},
     * 不再覆盖整个 login 方法,避免后续查询角色码时也被绕过(角色码必须按当前租户查)。
     * </p>
     */
    public TokenResponseVO login(LoginRequest request) {
        humanVerificationService.verify(request.getCaptchaToken());
        UserPO user = resolveSingleUser(userService.findByUsernameForLogin(request.getUsername()), ErrorCode.USER_NOT_FOUND);

        // 锁定前置检查:防止已锁定账号被持续尝试
        if (loginAttemptService.isLocked(request.getUsername(), user.getTenantId())) {
            log.warn("登录失败 - 账号已锁定: username={}, tenantId={}", request.getUsername(), user.getTenantId());
            throw new BusinessException(ErrorCode.USER_LOCKED);
        }

        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            long failures = loginAttemptService.recordFailure(request.getUsername(), user.getTenantId());
            log.warn("登录失败 - 密码错误: userId={}, tenantId={}, 累计失败次数={}", user.getId(), user.getTenantId(), failures);
            throw new BusinessException(ErrorCode.USER_PASSWORD_ERROR);
        }

        // 密码校验通过,重置失败计数防御窗口
        loginAttemptService.reset(request.getUsername(), user.getTenantId());
        return loginResolvedUser(user);
    }

    /**
     * 邮箱验证码登录。
     * <p>
     * <b>@TenantIgnore 位置</b>:仅作用于 {@code UserService.findByEmailForLogin},
     * 不覆盖后续角色码查询。
     * </p>
     */
    public TokenResponseVO emailLogin(EmailLoginRequest request) {
        humanVerificationService.verify(request.getCaptchaToken());
        String email = normalizeEmail(request.getEmail());
        UserPO user = resolveSingleUser(userService.findByEmailForLogin(email), ErrorCode.USER_NOT_FOUND);
        ensureUserEnabled(user);

        if (loginAttemptService.isLocked(email, user.getTenantId())) {
            log.warn("邮箱登录失败 - 账号已锁定: userId={}, tenantId={}", user.getId(), user.getTenantId());
            throw new BusinessException(ErrorCode.USER_LOCKED);
        }

        try {
            verifyEmailCode(email, request.getCode());
        } catch (BusinessException ex) {
            if (ex.getErrorCode() == ErrorCode.EMAIL_VERIFY_CODE_ERROR) {
                long failures = loginAttemptService.recordFailure(email, user.getTenantId());
                log.warn("邮箱登录失败 - 验证码错误: userId={}, tenantId={}, 累计失败次数={}", user.getId(), user.getTenantId(), failures);
            }
            throw ex;
        }

        loginAttemptService.reset(email, user.getTenantId());
        return loginResolvedUser(user);
    }

    /**
     * 发送邮箱验证码。
     */
    public void sendEmailCode(SendEmailCodeRequest request) {
        humanVerificationService.verify(request.getCaptchaToken());
        String email = normalizeEmail(request.getEmail());
        emailCodeSendRateLimitService.check(email);

        List<UserPO> users = userService.findByEmailForLogin(email);
        if (users == null || users.isEmpty()) {
            log.warn("邮箱验证码发送请求未匹配用户: email={}", maskEmail(email));
            return;
        }
        if (users.size() > 1) {
            log.warn("邮箱验证码发送请求匹配到多个租户: email={}", maskEmail(email));
            return;
        }
        UserPO user = users.get(0);
        if (user.getStatus() == StatusEnum.DISABLE) {
            log.warn("邮箱验证码发送请求命中禁用账号: userId={}, tenantId={}", user.getId(), user.getTenantId());
            return;
        }
        emailCodeService.send(email);
    }

    /**
     * 解析单一用户。
     */
    private UserPO resolveSingleUser(List<UserPO> users, ErrorCode notFoundErrorCode) {
        if (users == null || users.isEmpty()) {
            throw new BusinessException(notFoundErrorCode);
        }
        if (users.size() > 1) {
            throw new BusinessException(ErrorCode.USER_TENANT_AMBIGUOUS);
        }
        return users.get(0);
    }

    /**
     * 规范化邮箱。
     */
    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim();
    }

    /**
     * 脱敏邮箱。
     */
    private String maskEmail(String email) {
        String normalizedEmail = normalizeEmail(email);
        int atIndex = normalizedEmail.indexOf('@');
        if (atIndex <= 0) {
            return "***";
        }
        String localPart = normalizedEmail.substring(0, atIndex);
        String domain = normalizedEmail.substring(atIndex);
        if (localPart.length() == 1) {
            return "*" + domain;
        }
        return localPart.charAt(0) + "***" + domain;
    }

    /**
     * 校验邮箱验证码。
     */
    private void verifyEmailCode(String email, String code) {
        emailCodeService.verify(email, code);
    }

    /**
     * 确保用户启用。
     */
    private void ensureUserEnabled(UserPO user) {
        if (user.getStatus() == StatusEnum.DISABLE) {
            log.warn("登录失败 - 账号已禁用: userId={}, tenantId={}", user.getId(), user.getTenantId());
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }
    }

    /**
     * 登录已解析用户。
     */
    private TokenResponseVO loginResolvedUser(UserPO user) {
        ensureUserEnabled(user);
        SecurityTokenSession session = securityTokenSessionService.create(user);
        log.info("登录成功: userId={}, tenantId={}, isSuperAdmin={}",
                user.getId(), user.getTenantId(), session.isSuperAdmin());
        return securityTokenSessionService.toTokenResponse(session);
    }

    /**
     * 刷新访问令牌。
     */
    public TokenResponseVO refreshToken() {
        SecurityTokenSession session = securityTokenSessionService.refresh(SecurityHelper.getRequiredTokenValue());
        return securityTokenSessionService.toTokenResponse(session);
    }

    /**
     * 查询当前登录令牌。
     */
    public TokenResponseVO currentToken() {
        SecurityTokenSession session = securityTokenSessionService.load(SecurityHelper.getRequiredTokenValue())
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));
        return securityTokenSessionService.toTokenResponse(session);
    }

    /**
     * 退出当前会话。
     * <p>清除 Redis Token 会话。无登录态时调用也是幂等的,不抛异常。</p>
     */
    public void logout() {
        securityTokenSessionService.revoke(SecurityHelper.getTokenValue());
    }
}
