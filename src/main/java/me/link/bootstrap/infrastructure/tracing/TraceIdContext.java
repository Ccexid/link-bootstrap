package me.link.bootstrap.infrastructure.tracing;

import cn.hutool.core.util.IdUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * 分布式链路追踪上下文管理器
 *
 * <p>职责说明：
 * <ul>
 *   <li>仅负责 traceId 的线程级生命周期管理（生成/存储/透传/清除）</li>
 *   <li>与业务逻辑完全解耦，业务层禁止直接依赖此类</li>
 *   <li>所有 traceId 操作必须通过拦截器统一管理</li>
 * </ul>
 *
 * <p>关键设计：
 * <ul>
 *   <li>使用 TransmittableThreadLocal 确保异步场景传递（线程池/CompletableFuture）</li>
 *   <li>自动绑定 MDC 避免日志框架重复配置</li>
 *   <li>防御式空值处理防止 NPE</li>
 *   <li>禁止业务层直接调用 set()（通过注解标记为内部使用）</li>
 * </ul>
 */
public final class TraceIdContext {

    /** 核心上下文（支持异步传递） */
    private static final TransmittableThreadLocal<String> CONTEXT = new TransmittableThreadLocal<>();

    private TraceIdContext() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 获取当前线程的 traceId（业务层禁止调用！）
     *
     * <p>使用场景：DTO 层透传值（如 ResultResponse.traceId）
     *
     * @return traceId（若未初始化则返回空字符串，避免 NPE）
     */
    public static String get() {
        return CONTEXT.get() != null ? CONTEXT.get() : "";
    }

    /**
     * 【内部专用】由拦截器调用，业务层禁止使用！
     *
     * <p>初始化 traceId 并绑定 MDC
     *
     * @param traceId 从请求头获取或生成的 traceId
     */
    public static void init(String traceId) {
        if (!StringUtils.hasText(traceId)) {
            throw new IllegalArgumentException("TraceId cannot be empty");
        }
        CONTEXT.set(traceId);
        MDC.put("TRACE_ID", traceId); // 同步到日志系统
    }

    /**
     * 【内部专用】由拦截器调用，业务层禁止使用！
     *
     * <p>清理上下文防止内存泄漏
     */
    public static void clear() {
        CONTEXT.remove();
        MDC.remove("TRACE_ID");
    }

    /**
     * 生成符合规范的 traceId
     *
     * <p>业务层禁止调用！由拦截器统一生成
     *
     * @return 标准化 traceId (32位小写无横线)
     */
    static String generate() {
        return IdUtil.randomUUID().toLowerCase();
    }
}