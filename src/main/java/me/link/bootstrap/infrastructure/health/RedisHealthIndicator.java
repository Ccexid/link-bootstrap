package me.link.bootstrap.infrastructure.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * Redis连接健康检查器
 * <p>
 * 检查Redis连接状态和服务器信息,包括:
 * - 连接可用性(PING/PONG)
 * - Redis服务器版本
 * - 内存使用情况
 * - 客户端连接数
 * </p>
 *
 * @author Ccexid
 */
@Slf4j
@Component("redisHealth")
@RequiredArgsConstructor
public class RedisHealthIndicator implements HealthIndicator {

    private final RedisConnectionFactory redisConnectionFactory;

    /**
     * 返回当前组件健康状态。
     */
    @Override
    public Health health() {
        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            String pingResult = connection.ping();

            if ("PONG".equals(pingResult)) {
                Properties info = connection.info("server");
                Properties memory = connection.info("memory");
                Properties clients = connection.info("clients");

                return Health.up()
                        .withDetail("ping", pingResult)
                        .withDetail("redis_version", getOrDefault(info, "redis_version", "unknown"))
                        .withDetail("redis_mode", getOrDefault(info, "redis_mode", "unknown"))
                        .withDetail("tcp_port", getOrDefault(info, "tcp_port", "unknown"))
                        .withDetail("uptime_in_days", getOrDefault(info, "uptime_in_days", "0"))
                        .withDetail("used_memory_human", getOrDefault(memory, "used_memory_human", "unknown"))
                        .withDetail("max_memory_human", getOrDefault(memory, "max_memory_human", "unlimited"))
                        .withDetail("connected_clients", getOrDefault(clients, "connected_clients", "0"))
                        .withDetail("status", "连接正常")
                        .build();
            } else {
                return Health.down()
                        .withDetail("ping", pingResult)
                        .withDetail("status", "Redis响应异常")
                        .build();
            }
        } catch (Exception e) {
            log.error("Redis健康检查失败", e);
            return Health.down()
                    .withException(e)
                    .withDetail("error", "Redis连接失败: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 安全获取Properties属性值
     *
     * @param properties   Properties对象
     * @param key          属性键
     * @param defaultValue 默认值(当属性不存在或为null时返回)
     * @return 属性值或默认值
     */
    private String getOrDefault(Properties properties, String key, String defaultValue) {
        if (properties == null) {
            return defaultValue;
        }
        String value = properties.getProperty(key);
        return value != null ? value : defaultValue;
    }
}
