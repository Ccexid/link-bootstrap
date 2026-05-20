package me.link.bootstrap.infrastructure.config;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

/**
 * Undertow 服务器高性能优化配置
 *
 * @author Ccexid
 */
@Slf4j
@AutoConfiguration
// 1. 安全防护：只有当前应用是 Servlet 类型 Web 应用时才激活
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
// 2. 安全防护：只有类路径下切实存在 Undertow 核心类时才激活，防止其他切换成 Tomcat 的项目崩溃
@ConditionalOnClass({Undertow.class, UndertowServletWebServerFactory.class})
public class LinkUndertowAutoConfiguration implements WebServerFactoryCustomizer<UndertowServletWebServerFactory> {

    // 默认使用 CPU 核心数，允许通过 yml 配置文件进行精准调优
    @Value("${server.undertow.io-threads:0}")
    private int customIoThreads;

    @Value("${server.undertow.worker-threads:0}")
    private int customWorkerThreads;

    @Value("${server.undertow.no-request-timeout:30000}")
    private int noRequestTimeout;

    @Value("${server.undertow.buffer-size:16384}")
    private int bufferSize;

    @Override
    public void customize(UndertowServletWebServerFactory factory) {
        // 计算物理核心数
        int cores = Runtime.getRuntime().availableProcessors();

        // 如果没有配置，则使用推荐的动态计算公式
        int ioThreads = customIoThreads > 0 ? customIoThreads : cores;
        int workerThreads = customWorkerThreads > 0 ? customWorkerThreads : cores * 8;

        log.info("初始化 Undertow 核心参数调优 -> IO 线程数: [{}], Worker 线程数: [{}], 请求超时: [{}ms]",
                ioThreads, workerThreads, noRequestTimeout);

        factory.addBuilderCustomizers(builder -> {
            // 1. IO 线程数：主要用于处理非阻塞的 TCP 连接
            builder.setIoThreads(ioThreads);

            // 2. Worker 线程数：处理具体业务逻辑（阻塞操作如 DB/RPC）
            builder.setWorkerThreads(workerThreads);

            // 3. 设置连接空闲超时时间，防止大批死连接占用连接池资源
            builder.setServerOption(UndertowOptions.NO_REQUEST_TIMEOUT, noRequestTimeout);
            builder.setServerOption(UndertowOptions.IDLE_TIMEOUT, noRequestTimeout);

            // 4. 开启 HTTP/2 支持，提升多路复用性能
            builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true);

            // 5. 调整控制缓冲区的分配，稍微激进一点提升吞吐
            builder.setServerOption(UndertowOptions.MULTIPART_MAX_ENTITY_SIZE, 10L * 1024 * 1024); // 10MB
        });

        factory.addDeploymentInfoCustomizers(deploymentInfo -> {
            // 6. 安全地解决 Undertow 启动时默认生成的堆内存 Buffer Pool 警告
            // 注意：必须从属性中获取已存在的 info，如果不存在再创建，避免冲掉 Spring Boot 的默认 WebSocket 初始化
            WebSocketDeploymentInfo wsInfo = (WebSocketDeploymentInfo) deploymentInfo
                    .getServletContextAttributes()
                    .get(WebSocketDeploymentInfo.ATTRIBUTE_NAME);

            if (wsInfo == null) {
                wsInfo = new WebSocketDeploymentInfo();
                deploymentInfo.addServletContextAttribute(WebSocketDeploymentInfo.ATTRIBUTE_NAME, wsInfo);
            }

            // direct: true 表示使用堆外内存（Direct Memory），规避 JVM 频繁 GC 带来的停顿
            // bufferSize: 生产环境推荐 16KB (16384)
            wsInfo.setBuffers(new DefaultByteBufferPool(true, bufferSize));
        });
    }
}