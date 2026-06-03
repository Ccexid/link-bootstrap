package me.link.bootstrap.shared.kernel.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 客户端真实 IP 解析配置。
 *
 * <p>用于约束是否信任反向代理写入的转发头（如 X-Forwarded-For），
 * 默认关闭代理头信任，直接使用 {@code request.getRemoteAddr()}，
 * 避免攻击者通过自定义请求头伪造来源 IP。</p>
 *
 * <p>仅当服务真实部署在受信任的反向代理（Nginx / SLB / ALB 等）之后，
 * 且确认代理层会强制覆盖以下请求头时，才将 {@code trustForwardHeaders} 设置为 true。</p>
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "link.security.client-ip")
public class ClientIpProperties {

    /**
     * 是否信任反向代理写入的转发头，默认 false（不信任）。
     * <p>设为 true 时，将从 {@link #forwardHeaders} 列表里按顺序读取首个非空头，
     * 并在多级转发场景下取最右侧的 IP（最接近受信任代理的一跳）。</p>
     */
    private boolean trustForwardHeaders = false;

    /**
     * 允许读取的代理转发头列表，按数组顺序优先匹配。
     * <p>默认仅包含 {@code X-Forwarded-For}，可根据实际代理类型扩展。</p>
     */
    private List<String> forwardHeaders = List.of("X-Forwarded-For");
}
