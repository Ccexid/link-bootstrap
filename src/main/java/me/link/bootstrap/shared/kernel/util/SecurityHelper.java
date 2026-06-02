package me.link.bootstrap.shared.kernel.util;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 安全上下文工具类，用于获取当前登录用户信息。
 * <p>
 * 基于 Sa-Token 框架，提供从当前会话中获取用户ID、租户ID等信息的静态方法。
 * 适用于 Application Service 层、AOP 切面等需要获取当前用户上下文的场景。
 * </p>
 *
 * @author ccexid
 */
@Slf4j
public final class SecurityHelper {

    private SecurityHelper() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }

    /**
     * 获取当前登录用户的 ID。
     *
     * @return 用户 ID（未登录时返回 null）
     */
    public static Long getUserId() {
        try {
            if (StpUtil.isLogin()) {
                return StpUtil.getLoginIdAsLong();
            }
            return null;
        } catch (Exception e) {
            log.warn("获取当前用户ID失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取当前登录用户的租户 ID。
     * <p>
     * 从 Sa-Token Session 中读取 "tenantId" 属性。
     * 需要在用户登录时将 tenantId 存入 Session。
     * </p>
     *
     * @return 租户 ID（未登录或未设置时返回 null）
     */
    public static Long getTenantId() {
        try {
            if (StpUtil.isLogin()) {
                Object tenantId = StpUtil.getSession().get("tenantId");
                if (tenantId != null) {
                    return Long.valueOf(tenantId.toString());
                }
                log.warn("当前用户Session中未找到tenantId");
                return null;
            }
            return null;
        } catch (Exception e) {
            log.warn("获取当前用户租户ID失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取当前登录用户的 ID，如果未登录则抛出异常。
     *
     * @return 用户 ID
     * @throws IllegalStateException 用户未登录时抛出
     */
    public static Long getRequiredUserId() {
        Long userId = getUserId();
        if (userId == null) {
            throw new IllegalStateException("当前用户未登录");
        }
        return userId;
    }

    /**
     * 获取当前登录用户的租户 ID，如果未登录或未设置则抛出异常。
     *
     * @return 租户 ID
     * @throws IllegalStateException 用户未登录或租户ID未设置时抛出
     */
    public static Long getRequiredTenantId() {
        Long tenantId = getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("当前用户未登录或租户ID未设置");
        }
        return tenantId;
    }

    /**
     * 判断当前用户是否已登录。
     *
     * @return true-已登录, false-未登录
     */
    public static boolean isLoggedIn() {
        return StpUtil.isLogin();
    }
}
