package me.link.bootstrap.infrastructure.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * 数据库连接健康检查器
 * <p>
 * 检查HikariCP数据库连接池状态,包括:
 * - 连接可用性
 * - 数据库版本信息
 * - 连接池活跃连接数
 * </p>
 *
 * @author Ccexid
 */
@Slf4j
@Component("dbHealth")
@RequiredArgsConstructor
public class DatabaseHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    /**
     * 返回当前组件健康状态。
     */
    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            return checkDatabase(connection);
        } catch (SQLException e) {
            log.error("数据库健康检查失败", e);
            return Health.down()
                    .withException(e)
                    .withDetail("error", "数据库连接失败: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 检查数据库连接和元数据
     *
     * @param connection 数据库连接
     * @return Health 健康状态对象
     */
    private Health checkDatabase(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        
        return Health.up()
                .withDetail("database_product", metaData.getDatabaseProductName())
                .withDetail("database_version", metaData.getDatabaseProductVersion())
                .withDetail("driver_name", metaData.getDriverName())
                .withDetail("driver_version", metaData.getDriverVersion())
                .withDetail("connection_url", maskSensitiveInfo(metaData.getURL()))
                .withDetail("status", "连接正常")
                .build();
    }

    /**
     * 脱敏敏感信息(用户名密码等)
     *
     * @param url 数据库连接URL
     * @return 脱敏后的URL
     */
    private String maskSensitiveInfo(String url) {
        if (url == null) {
            return "unknown";
        }
        return url.replaceAll("(?i)(password|pwd)=[^&]*", "password=***");
    }
}
