package me.link.bootstrap.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.link.bootstrap.infrastructure.mapper.PermissionMapper;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

/**
 * 权限码 / 角色码缓存服务。
 * <p>
 * 在 Redis 中按用户 ID 缓存权限码列表和角色码列表(TTL 30 分钟),
 * 减少高频接口注解校验(@PreAuthorize)时的 SQL 压力。
 * </p>
 * <p>
 * <b>缓存键设计</b>:
 * <ul>
 *   <li>{@code link:perm:{userId}} —— 权限码列表;</li>
 *   <li>{@code link:role-code:{userId}} —— 角色码列表。</li>
 * </ul>
 * 由于 DDL 中 system_users.id 是全局唯一(自增主键),用户与租户是 1:1 绑定,
 * 故缓存键无需引入 tenantId 维度。
 * </p>
 * <p>
 * <b>失效策略</b>(由 Application Service 在写操作后主动调用):
 * <ul>
 *   <li>{@link #evictByUserId} —— 单用户失效,用于 user_role 变更;</li>
 *   <li>{@link #evictByRoleId} —— 按角色级联到所有关联用户,用于 role / role_menu 变更;</li>
 *   <li>{@link #evictAll} —— 全量失效(慎用,基于 SCAN+DEL,大数据量下阻塞),
 *       仅用于 menu 表变更(菜单是全局表,变更影响所有用户)。</li>
 * </ul>
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionCacheService {

    private static final Duration TTL = Duration.ofMinutes(30);

    private static final String KEY_PREFIX_PERM = "link:perm:";
    private static final String KEY_PREFIX_ROLE_CODE = "link:role-code:";

    private final RedissonClient redissonClient;
    private final PermissionMapper permissionMapper;

    /**
     * 查询权限码列表,优先读缓存,miss 时回源数据库并回写。
     */
    public List<String> getPermissions(Long userId) {
        if (userId == null) {
            return List.of();
        }
        RBucket<List<String>> bucket = redissonClient.getBucket(permKey(userId));
        List<String> cached = bucket.get();
        if (cached != null) {
            return cached;
        }
        List<String> loaded = permissionMapper.selectPermissionsByUserId(userId);
        bucket.set(loaded, TTL);
        return loaded;
    }

    /**
     * 查询角色码列表,优先读缓存,miss 时回源数据库并回写。
     */
    public List<String> getRoleCodes(Long userId) {
        if (userId == null) {
            return List.of();
        }
        RBucket<List<String>> bucket = redissonClient.getBucket(roleKey(userId));
        List<String> cached = bucket.get();
        if (cached != null) {
            return cached;
        }
        List<String> loaded = permissionMapper.selectRoleCodesByUserId(userId);
        bucket.set(loaded, TTL);
        return loaded;
    }

    /**
     * 失效单个用户的权限/角色缓存(用于 user_role 变更后)。
     */
    public void evictByUserId(Long userId) {
        if (userId == null) {
            return;
        }
        redissonClient.getBucket(permKey(userId)).delete();
        redissonClient.getBucket(roleKey(userId)).delete();
        log.debug("权限缓存已失效: userId={}", userId);
    }

    /**
     * 失效拥有指定角色的所有用户的缓存(用于 role / role_menu 变更后)。
     * <p>
     * 当前 Spring Security 上下文需要登录且有 tenantId,user_role 查询会被
     * TenantLineInnerInterceptor 自动加 tenant_id 过滤;若调用方是超管则不受租户限制。
     * </p>
     */
    public void evictByRoleId(Long roleId) {
        if (roleId == null) {
            return;
        }
        List<Long> userIds = permissionMapper.selectUserIdsByRoleId(roleId);
        if (userIds.isEmpty()) {
            return;
        }
        userIds.forEach(this::evictByUserId);
        log.debug("权限缓存按角色失效: roleId={}, 受影响用户数={}", roleId, userIds.size());
    }

    /**
     * 全量失效(SCAN + DEL,大数据量阻塞,慎用)。
     * <p>用于菜单大改后强制刷新所有用户权限。</p>
     */
    public void evictAll() {
        long permRemoved = redissonClient.getKeys().deleteByPattern(KEY_PREFIX_PERM + "*");
        long roleRemoved = redissonClient.getKeys().deleteByPattern(KEY_PREFIX_ROLE_CODE + "*");
        log.warn("权限缓存全量失效: perm 删除={}, role-code 删除={}", permRemoved, roleRemoved);
    }

    /**
     * 构建权限键。
     */
    private String permKey(Long userId) {
        return KEY_PREFIX_PERM + userId;
    }

    /**
     * 构建角色键。
     */
    private String roleKey(Long userId) {
        return KEY_PREFIX_ROLE_CODE + userId;
    }
}
