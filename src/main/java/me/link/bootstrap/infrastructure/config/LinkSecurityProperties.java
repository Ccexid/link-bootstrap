package me.link.bootstrap.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 安全相关配置项,绑定 {@code link.security.*} 配置。
 * <p>配置示例:</p>
 * <pre>
 * link:
 *   security:
 *     login:
 *       max-fail-attempts: 5
 *       lock-duration: 15m
 * </pre>
 */
@Data
@Component
@ConfigurationProperties(prefix = "link.security")
public class LinkSecurityProperties {

    /** 登录策略配置。 */
    private Login login = new Login();

    @Data
    public static class Login {
        /** 触发账号锁定的连续失败次数阈值。 */
        private int maxFailAttempts = 5;

        /** 账号锁定持续时长,默认 15 分钟。 */
        private Duration lockDuration = Duration.ofMinutes(15);
    }
}
