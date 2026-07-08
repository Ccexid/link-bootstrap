package me.link.bootstrap.shared.kernel.util;

import lombok.extern.slf4j.Slf4j;
import me.link.bootstrap.infrastructure.security.LoginUserPrincipal;
import me.link.bootstrap.shared.kernel.constant.SecurityConstants;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全上下文工具类，用于获取当前登录用户信息。
 * <p>
 * 基于 Spring Security 上下文，提供从当前认证主体中获取用户ID、租户ID等信息的静态方法。
 * 适用于 Application Service 层、AOP 切面等需要获取当前用户上下文的场景。
 * </p>
 */
@Slf4j
public final class SecurityHelper {

    /**
     * 创建安全实例。
     */
    private SecurityHelper() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }

    /**
     * 获取当前登录用户的 ID。
     *
     * @return 用户 ID（未登录时返回 null）
     */
    public static Long getUserId() {
        LoginUserPrincipal principal = currentPrincipal();
        return principal == null ? null : principal.getUserId();
    }

    /**
     * 获取当前登录用户的租户 ID。
     *
     * @return 租户 ID（未登录或未设置时返回 null）
     */
    public static Long getTenantId() {
        LoginUserPrincipal principal = currentPrincipal();
        return principal == null ? null : principal.getTenantId();
    }

    /**
     * 判断当前登录用户是否平台超级管理员。
     * <p>
     * 登录时由 {@code AuthService} 写入 Redis Token 会话,
     * 持有 {@link SecurityConstants#ROLE_SUPER_ADMIN} 角色码者为 true。
     * </p>
     * <p>
     * <b>注意</b>:此方法被 {@code LinkTenantLineHandler} 在每条 SQL 解析时调用,
     * 处于性能敏感路径。当前实现只读取 ThreadLocal 中的 Authentication,不访问 Redis。
     * </p>
     *
     * @return true-超管, false-非超管或未登录
     */
    public static boolean isSuperAdmin() {
        LoginUserPrincipal principal = currentPrincipal();
        return principal != null && principal.isSuperAdmin();
    }

    /**
     * 获取当前登录用户的 ID，如果未登录则抛出异常。
     *
     * @return 用户 ID
     */
    public static Long getRequiredUserId() {
        Long userId = getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "当前用户未登录");
        }
        return userId;
    }

    /**
     * 获取当前登录用户的租户 ID，如果未登录或未设置则抛出异常。
     *
     * @return 租户 ID
     */
    public static Long getRequiredTenantId() {
        Long tenantId = getTenantId();
        if (tenantId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "当前用户未登录或租户ID未设置");
        }
        return tenantId;
    }

    /**
     * 判断当前用户是否已登录。
     *
     * @return true-已登录, false-未登录
     */
    public static boolean isLoggedIn() {
        return currentPrincipal() != null;
    }

    /**
     * 获取当前请求的 Bearer Token 值。
     *
     * @return Token 值（未登录时返回 null）
     */
    public static String getTokenValue() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getCredentials() instanceof String token)) {
            return null;
        }
        return token;
    }

    /**
     * 获取必需的令牌值。
     */
    public static String getRequiredTokenValue() {
        String token = getTokenValue();
        if (token == null || token.isBlank()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "当前用户未登录");
        }
        return token;
    }

    /**
     * 获取当前主体。
     */
    private static LoginUserPrincipal currentPrincipal() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return null;
            }
            if (authentication.getPrincipal() instanceof LoginUserPrincipal principal) {
                return principal;
            }
            return null;
        } catch (Exception e) {
            log.warn("获取当前登录主体失败: {}", e.getMessage());
            return null;
        }
    }
}
