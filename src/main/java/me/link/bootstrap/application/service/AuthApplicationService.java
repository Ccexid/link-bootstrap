package me.link.bootstrap.application.service;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.BCrypt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.link.bootstrap.application.command.EmailLoginCommand;
import me.link.bootstrap.application.command.LoginCommand;
import me.link.bootstrap.application.command.SendEmailCodeCommand;
import me.link.bootstrap.application.command.TokenRefreshResult;
import me.link.bootstrap.domain.valueobject.StatusEnum;
import me.link.bootstrap.infrastructure.persistence.mapper.PermissionMapper;
import me.link.bootstrap.infrastructure.persistence.po.UserPO;
import me.link.bootstrap.infrastructure.security.EmailCodeService;
import me.link.bootstrap.infrastructure.security.HumanVerificationService;
import me.link.bootstrap.infrastructure.security.LoginAttemptService;
import me.link.bootstrap.shared.kernel.constant.SecurityConstants;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 认证应用服务,负责登录、登出的业务编排。
 * <p>
 * 账号密码登录流程:按 username 跨租户查询唯一用户 → BCrypt 校验密码 → 校验账号状态 →
 * 调用 Sa-Token 创建会话 → 在 Session 注入 tenantId / userType,供后续多租户隔离与权限判断使用。
 * </p>
 * <p>
 * <b>@TenantIgnore</b>:登录时尚未建立 Sa-Token 会话,
 * {@link me.link.bootstrap.shared.kernel.database.mybatis.LinkTenantLineHandler} 取不到 tenantId,
 * 默认行为会让查询 SQL 退化为 {@code tenant_id IS NULL} 而查不到数据。
 * 登录查询由用户应用服务的最小查询方法显式标注绕过租户拦截,认证成功后再将用户所属 tenantId 写入 Session。
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthApplicationService {

    private final UserApplicationService userApplicationService;
    private final PermissionMapper permissionMapper;
    private final LoginAttemptService loginAttemptService;
    private final EmailCodeService emailCodeService;
    private final HumanVerificationService humanVerificationService;

    /**
     * 账号密码登录。
     * <p>
     * 错误响应均统一为业务异常,HTTP 状态码由 GlobalExceptionHandler 处理。
     * 登录成功后会将 tenantId、userType、isSuperAdmin 写入 Sa-Token Session,
     * 供 SecurityHelper / LinkTenantLineHandler 在后续请求中使用。
     * </p>
     * <p>
     * <b>@TenantIgnore 位置</b>:仅作用于 {@code UserApplicationService.findByUsernameForLogin},
     * 不再覆盖整个 login 方法,避免后续查询角色码时也被绕过(角色码必须按当前租户查)。
     * </p>
     */
    public void login(LoginCommand command) {
        humanVerificationService.verify(command.captchaToken());
        UserPO user = resolveSingleUser(userApplicationService.findByUsernameForLogin(command.username()), ErrorCode.USER_NOT_FOUND);

        // 锁定前置检查:防止已锁定账号被持续尝试
        if (loginAttemptService.isLocked(command.username(), user.getTenantId())) {
            log.warn("登录失败 - 账号已锁定: username={}, tenantId={}", command.username(), user.getTenantId());
            throw new BusinessException(ErrorCode.USER_LOCKED);
        }

        if (!BCrypt.checkpw(command.password(), user.getPassword())) {
            long failures = loginAttemptService.recordFailure(command.username(), user.getTenantId());
            log.warn("登录失败 - 密码错误: userId={}, tenantId={}, 累计失败次数={}", user.getId(), user.getTenantId(), failures);
            throw new BusinessException(ErrorCode.USER_PASSWORD_ERROR);
        }

        // 密码校验通过,重置失败计数防御窗口
        loginAttemptService.reset(command.username(), user.getTenantId());
        loginResolvedUser(user);
    }

    /**
     * 邮箱验证码登录。
     * <p>
     * <b>@TenantIgnore 位置</b>:仅作用于 {@code UserApplicationService.findByEmailForLogin},
     * 不覆盖后续角色码查询。
     * </p>
     */
    public void emailLogin(EmailLoginCommand command) {
        humanVerificationService.verify(command.captchaToken());
        String email = normalizeEmail(command.email());
        UserPO user = resolveSingleUser(userApplicationService.findByEmailForLogin(email), ErrorCode.USER_NOT_FOUND);
        ensureUserEnabled(user);

        if (loginAttemptService.isLocked(email, user.getTenantId())) {
            log.warn("邮箱登录失败 - 账号已锁定: userId={}, tenantId={}", user.getId(), user.getTenantId());
            throw new BusinessException(ErrorCode.USER_LOCKED);
        }

        try {
            verifyEmailCode(command);
        } catch (BusinessException ex) {
            if (ex.getErrorCode() == ErrorCode.EMAIL_VERIFY_CODE_ERROR) {
                long failures = loginAttemptService.recordFailure(email, user.getTenantId());
                log.warn("邮箱登录失败 - 验证码错误: userId={}, tenantId={}, 累计失败次数={}", user.getId(), user.getTenantId(), failures);
            }
            throw ex;
        }

        loginAttemptService.reset(email, user.getTenantId());
        loginResolvedUser(user);
    }

    /**
     * 发送邮箱验证码。
     */
    public void sendEmailCode(SendEmailCodeCommand command) {
        String email = normalizeEmail(command.email());
        UserPO user = resolveSingleUser(userApplicationService.findByEmailForLogin(email), ErrorCode.USER_NOT_FOUND);
        ensureUserEnabled(user);
        emailCodeService.send(email);
    }

    private UserPO resolveSingleUser(List<UserPO> users, ErrorCode notFoundErrorCode) {
        if (users == null || users.isEmpty()) {
            throw new BusinessException(notFoundErrorCode);
        }
        if (users.size() > 1) {
            throw new BusinessException(ErrorCode.USER_TENANT_AMBIGUOUS);
        }
        return users.get(0);
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim();
    }

    private void verifyEmailCode(EmailLoginCommand command) {
        emailCodeService.verify(command.email(), command.code());
    }

    private void ensureUserEnabled(UserPO user) {
        if (user.getStatus() == StatusEnum.DISABLE) {
            log.warn("登录失败 - 账号已禁用: userId={}, tenantId={}", user.getId(), user.getTenantId());
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }
    }

    private void loginResolvedUser(UserPO user) {
        ensureUserEnabled(user);

        StpUtil.login(user.getId());
        StpUtil.getSession().set(SecurityConstants.SESSION_KEY_TENANT_ID, user.getTenantId());
        StpUtil.getSession().set(SecurityConstants.SESSION_KEY_USER_TYPE, user.getUserType());

        // 标记是否为平台超级管理员,LinkTenantLineHandler 会据此跳过租户隔离
        List<String> roleCodes = permissionMapper.selectRoleCodesByUserId(user.getId());
        boolean isSuperAdmin = roleCodes.contains(SecurityConstants.ROLE_SUPER_ADMIN);
        StpUtil.getSession().set(SecurityConstants.SESSION_KEY_SUPER_ADMIN, isSuperAdmin);

        log.info("登录成功: userId={}, tenantId={}, isSuperAdmin={}", user.getId(), user.getTenantId(), isSuperAdmin);
    }

    public TokenRefreshResult refreshToken() {
        StpUtil.checkLogin();
        long timeout = SaManager.getConfig().getTimeout();
        if (timeout > 0) {
            StpUtil.renewTimeout(timeout);
        }
        StpUtil.updateLastActiveToNow();
        return currentToken();
    }

    public TokenRefreshResult currentToken() {
        return new TokenRefreshResult(
                StpUtil.getTokenName(),
                StpUtil.getTokenValue(),
                SaManager.getConfig().getTokenPrefix(),
                StpUtil.getTokenTimeout(),
                StpUtil.getTokenActiveTimeout()
        );
    }

    /**
     * 退出当前会话。
     * <p>清除 Sa-Token 会话(含 Redis Session)。无登录态时调用也是幂等的,不抛异常。</p>
     */
    public void logout() {
        StpUtil.logout();
    }
}
