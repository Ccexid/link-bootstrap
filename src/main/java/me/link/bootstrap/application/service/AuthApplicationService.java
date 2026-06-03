package me.link.bootstrap.application.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.BCrypt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.link.bootstrap.application.command.LoginCommand;
import me.link.bootstrap.domain.entity.UserEntity;
import me.link.bootstrap.domain.repository.UserRepository;
import me.link.bootstrap.domain.valueobject.StatusEnum;
import me.link.bootstrap.infrastructure.persistence.mapper.PermissionMapper;
import me.link.bootstrap.infrastructure.security.LoginAttemptService;
import me.link.bootstrap.shared.kernel.constant.SecurityConstants;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 认证应用服务,负责登录、登出的业务编排。
 * <p>
 * 登录流程:按 (username, tenantId) 双条件查询用户 → BCrypt 校验密码 → 校验账号状态 →
 * 调用 Sa-Token 创建会话 → 在 Session 注入 tenantId / userType,供后续多租户隔离与权限判断使用。
 * </p>
 * <p>
 * <b>@TenantIgnore</b>:登录时尚未建立 Sa-Token 会话,
 * {@link me.link.bootstrap.shared.kernel.database.mybatis.LinkTenantLineHandler} 取不到 tenantId,
 * 默认行为会让查询 SQL 退化为 {@code tenant_id IS NULL} 而查不到数据。
 * 此处显式标注绕过租户拦截,改由方法参数中的 tenantId 作为业务条件参与查询。
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthApplicationService {

    private final UserRepository userRepository;
    private final PermissionMapper permissionMapper;
    private final LoginAttemptService loginAttemptService;

    /**
     * 登录并返回 Sa-Token 颁发的 token。
     * <p>
     * 错误响应均统一为业务异常,HTTP 状态码由 GlobalExceptionHandler 处理。
     * 登录成功后会将 tenantId、userType、isSuperAdmin 写入 Sa-Token Session,
     * 供 SecurityHelper / LinkTenantLineHandler 在后续请求中使用。
     * </p>
     * <p>
     * <b>@TenantIgnore 位置</b>:仅作用于 {@code UserRepositoryImpl.findByUsernameAndTenantId},
     * 不再覆盖整个 login 方法,避免后续查询角色码时也被绕过(角色码必须按当前租户查)。
     * </p>
     *
     * @return Sa-Token token 值(不含 token 名前缀)
     */
    public String login(LoginCommand command) {
        // 锁定前置检查:防止已锁定账号被持续尝试
        if (loginAttemptService.isLocked(command.username(), command.tenantId())) {
            log.warn("登录失败 - 账号已锁定: username={}, tenantId={}", command.username(), command.tenantId());
            throw new BusinessException(ErrorCode.USER_LOCKED);
        }

        UserEntity user = userRepository.findByUsernameAndTenantId(command.username(), command.tenantId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!BCrypt.checkpw(command.password(), user.getPassword())) {
            long failures = loginAttemptService.recordFailure(command.username(), command.tenantId());
            log.warn("登录失败 - 密码错误: userId={}, tenantId={}, 累计失败次数={}", user.getId(), user.getTenantId(), failures);
            throw new BusinessException(ErrorCode.USER_PASSWORD_ERROR);
        }

        if (user.getStatus() == StatusEnum.DISABLE) {
            log.warn("登录失败 - 账号已禁用: userId={}, tenantId={}", user.getId(), user.getTenantId());
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }

        // 密码校验通过,重置失败计数防御窗口
        loginAttemptService.reset(command.username(), command.tenantId());
        StpUtil.login(user.getId());
        StpUtil.getSession().set(SecurityConstants.SESSION_KEY_TENANT_ID, user.getTenantId());
        StpUtil.getSession().set(SecurityConstants.SESSION_KEY_USER_TYPE, user.getUserType());

        // 标记是否为平台超级管理员,LinkTenantLineHandler 会据此跳过租户隔离
        List<String> roleCodes = permissionMapper.selectRoleCodesByUserId(user.getId());
        boolean isSuperAdmin = roleCodes.contains(SecurityConstants.ROLE_SUPER_ADMIN);
        StpUtil.getSession().set(SecurityConstants.SESSION_KEY_SUPER_ADMIN, isSuperAdmin);

        log.info("登录成功: userId={}, tenantId={}, isSuperAdmin={}", user.getId(), user.getTenantId(), isSuperAdmin);
        return StpUtil.getTokenValue();
    }

    /**
     * 退出当前会话。
     * <p>清除 Sa-Token 会话(含 Redis Session)。无登录态时调用也是幂等的,不抛异常。</p>
     */
    public void logout() {
        StpUtil.logout();
    }
}
