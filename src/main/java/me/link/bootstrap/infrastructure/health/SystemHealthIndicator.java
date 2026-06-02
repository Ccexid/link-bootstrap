package me.link.bootstrap.infrastructure.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 系统综合健康检查器
 * <p>
 * 聚合所有核心组件的健康状态,提供系统整体健康视图:
 * - 数据库连接状态
 * - Redis连接状态
 * - 系统运行时长
 * - 检查时间戳
 * </p>
 * <p>
 * 访问端点: GET /actuator/health
 * </p>
 *
 * @author Ccexid
 */
@Slf4j
@Component("systemHealth")
@RequiredArgsConstructor
public class SystemHealthIndicator implements HealthIndicator {

    private final DatabaseHealthIndicator databaseHealthIndicator;
    private final RedisHealthIndicator redisHealthIndicator;

    /**
     * 应用启动时间
     */
    private static final LocalDateTime START_TIME = LocalDateTime.now();

    /**
     * 返回当前组件健康状态。
     */
    @Override
    public Health health() {
        Map<String, Object> details = new LinkedHashMap<>();
        
        Health dbHealth = databaseHealthIndicator.health();
        Health redisHealth = redisHealthIndicator.health();
        
        details.put("database", dbHealth.getDetails());
        details.put("redis", redisHealth.getDetails());
        details.put("uptime_seconds", calculateUptimeSeconds());
        details.put("check_time", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        details.put("application_name", "top-link-mall");
        
        boolean isHealthy = dbHealth.getStatus().equals(Health.up().build().getStatus())
                && redisHealth.getStatus().equals(Health.up().build().getStatus());
        
        return isHealthy 
                ? Health.up().withDetails(details).build()
                : Health.down().withDetails(details).build();
    }

    /**
     * 计算应用运行时长(秒)
     *
     * @return 运行时长秒数
     */
    private long calculateUptimeSeconds() {
        return java.time.Duration.between(START_TIME, LocalDateTime.now()).getSeconds();
    }
}
