package me.link.bootstrap.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Undertow 容器参数配置。
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "server.undertow")
public class LinkUndertowProperties {

    /** IO 线程数。小于等于 0 时按 CPU 核心数自动计算。 */
    private int ioThreads = 0;

    /** Worker 线程数。小于等于 0 时按 CPU 核心数自动计算。 */
    private int workerThreads = 0;

    /** 请求空闲超时时间,单位毫秒。 */
    private int noRequestTimeout = 30000;

    /** WebSocket Buffer 大小。 */
    private int bufferSize = 16384;

    /** Multipart 最大实体大小。 */
    private long multipartMaxEntitySize = 16777216L;
}
