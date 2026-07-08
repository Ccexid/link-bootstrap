package me.link.bootstrap.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.link.bootstrap.infrastructure.config.LinkSecurityProperties;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

/**
 * 登录失败计数与账号锁定服务。
 * <p>
 * 在 Redis 中维护 {@code (tenantId, username)} 维度的连续失败计数,
 * 达到阈值后视为账号锁定,直到计数器 TTL 自动过期。
 * </p>
 * <p>
 * <b>计数器键</b>:{@code link:login:fail:{tenantId}:{username}}<br>
 * <b>首次失败</b>:计数 = 1 并设置 TTL = 锁定时长。<br>
 * <b>后续失败</b>:计数 += 1,TTL 不重置(即从首次失败开始计窗口期)。<br>
 * <b>登录成功</b>:删除计数器,重置防御窗口。
 * </p>
 * <p>
 * <b>并发说明</b>:{@code incrementAndGet()} 与 {@code expire()} 不在同一原子操作中,
 * 极小概率下首次失败的 expire 可能落后于第二次失败,使 TTL 等同于"第二次失败起"开始计时。
 * 对安全语义无实质影响,可接受。
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private static final String KEY_PREFIX = "link:login:fail:";

    private final RedissonClient redissonClient;
    private final LinkSecurityProperties securityProperties;

    /**
     * 判断指定 (租户, 用户名) 是否处于锁定状态。
     */
    public boolean isLocked(String username, Long tenantId) {
        long count = currentCount(username, tenantId);
        return count >= securityProperties.getLogin().getMaxFailAttempts();
    }

    /**
     * 记录一次登录失败。
     *
     * @return 失败后的累计次数
     */
    public long recordFailure(String username, Long tenantId) {
        RAtomicLong counter = redissonClient.getAtomicLong(buildKey(username, tenantId));
        long current = counter.incrementAndGet();
        if (current == 1L) {
            counter.expire(securityProperties.getLogin().getLockDuration());
        }
        return current;
    }

    /**
     * 登录成功后重置失败计数。
     */
    public void reset(String username, Long tenantId) {
        redissonClient.getAtomicLong(buildKey(username, tenantId)).delete();
    }

    /**
     * 取得当前累计失败次数(供日志或限流提示使用)。
     */
    public long currentCount(String username, Long tenantId) {
        return redissonClient.getAtomicLong(buildKey(username, tenantId)).get();
    }

    /**
     * 构建键。
     */
    private String buildKey(String username, Long tenantId) {
        return KEY_PREFIX + tenantId + ":" + username;
    }
}
