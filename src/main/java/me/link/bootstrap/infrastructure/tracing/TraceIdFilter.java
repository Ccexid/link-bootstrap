package me.link.bootstrap.infrastructure.tracing;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static me.link.bootstrap.shared.kernel.constant.GlobalConstants.TRACE_ID_HEADER;

/**
 * TraceId 链路追踪过滤器
 * <p>
 * 在每个 HTTP 请求入口处提取或生成 TraceId，并绑定到当前线程上下文，
 * 确保整个请求生命周期内的日志都包含相同的 TraceId，便于分布式链路追踪。
 * </p>
 * <p>
 * 执行顺序：最高优先级（{@link Ordered#HIGHEST_PRECEDENCE}），确保在其他过滤器和业务逻辑之前初始化 TraceId。
 * </p>
 * <p>
 * 注意：此过滤器仅同步请求有效。如果使用异步 Servlet 或 CompletableFuture 等异步特性，
 * 需要在异步任务中手动传递 TraceId 上下文。
 * </p>
 *
 * @author Ccexid
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class TraceIdFilter extends OncePerRequestFilter {

    /**
     * 过滤请求，初始化 TraceId 上下文并在请求结束后清理
     *
     * @param request     HTTP 请求对象
     * @param response    HTTP 响应对象
     * @param filterChain 过滤器链
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String traceId = null;
        try {
            traceId = extractTraceId(request);
            TraceIdContext.init(traceId);

            // 将 TraceId 注入响应头，方便客户端记录和关联
            response.setHeader(TRACE_ID_HEADER, traceId);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("请求处理异常, traceId: {}, uri: {}", traceId, request.getRequestURI(), e);
            throw e;
        } finally {
            TraceIdContext.clear();
        }
    }

    /**
     * 从 HTTP 请求中提取或生成 TraceId
     * <p>
     * 优先从请求头 " " 中获取追踪 ID，如果不存在或为空，
     * 则自动生成一个新的 TraceId。
     * </p>
     *
     * @param request HTTP 请求对象，用于获取请求头中的 TraceId
     * @return 提取到的 TraceId 或新生成的 TraceId 字符串，保证不为 null 或空
     */
    private String extractTraceId(HttpServletRequest request) {
        String headerId = request.getHeader(TRACE_ID_HEADER);
        if (StringUtils.isNotBlank(headerId)) {
            return headerId;
        }
        return TraceIdContext.generate();
    }
}
