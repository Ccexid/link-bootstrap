package me.link.bootstrap.infrastructure.security;

import cn.dev33.satoken.stp.StpInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Sa-Token 权限码 / 角色码加载器。
 * <p>
 * Sa-Token 在执行 {@code @SaCheckPermission("xxx")} 或 {@code @SaCheckRole("xxx")} 注解时,
 * 通过此实现获取当前登录用户拥有的权限码与角色码列表。
 * </p>
 * <p>
 * 数据来源:{@code system_user_role} - {@code system_role_menu} - {@code system_menu} 三表 JOIN,
 * 由 {@code PermissionMapper} 提供查询能力。租户隔离由 MyBatis-Plus
 * {@code TenantLineInnerInterceptor} 自动追加 {@code tenant_id} 条件,
 * 全局表 {@code system_menu} 在 {@code LinkTenantLineHandler} 的忽略清单中。
 * </p>
 * <p>
 * <b>性能提示</b>:权限列表每次注解校验都会触发一次 SQL,后续可在此层接入 Redisson 缓存
 * (Key 建议 {@code perm:{userId}:{tenantId}}, TTL 与 Sa-Token active-timeout 对齐) 降低数据库压力。
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final PermissionCacheService permissionCacheService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        Long userId = parseUserId(loginId);
        if (userId == null) {
            return Collections.emptyList();
        }
        return permissionCacheService.getPermissions(userId);
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Long userId = parseUserId(loginId);
        if (userId == null) {
            return Collections.emptyList();
        }
        return permissionCacheService.getRoleCodes(userId);
    }

    /**
     * Sa-Token 的 loginId 来源于 {@code StpUtil.login(Object)} 时传入的对象,
     * 这里统一按 Long 解析。非法值时回退空列表,避免拖累 Sa-Token AOP 链路。
     */
    private Long parseUserId(Object loginId) {
        if (loginId == null) {
            return null;
        }
        try {
            return Long.valueOf(loginId.toString());
        } catch (NumberFormatException e) {
            log.warn("无法将 loginId 解析为 userId: {}", loginId);
            return null;
        }
    }
}
