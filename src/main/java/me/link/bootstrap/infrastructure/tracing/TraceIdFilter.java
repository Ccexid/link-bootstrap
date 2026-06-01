package me.link.bootstrap.infrastructure.tracing;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Pattern;

import static me.link.bootstrap.shared.kernel.constant.GlobalConstants.TRACE_ID_HEADER;

/**
 * TraceId 链路追踪过滤器
 * <p>
 * 在每个 HTTP 请求入口处提取或生成 TraceId，并绑定到当前线程上下文。
 * </p>
 *
 * @author Ccexid
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceIdFilter extends OncePerRequestFilter {

    private static final int MAX_TRACE_ID_LENGTH = 64;

    private static final Pattern TRACE_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");

    /**
     * 过滤请求，初始化 TraceId 上下文并在请求结束后清理。
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
        String traceId = extractTraceId(request);
        try {
            TraceIdContext.init(traceId);
            request.setAttribute(TRACE_ID_HEADER, traceId);
            response.setHeader(TRACE_ID_HEADER, traceId);

            filterChain.doFilter(request, response);
        } finally {
            TraceIdContext.clear();
        }
    }

    /**
     * 从 HTTP 请求中提取或生成 TraceId。
     * <p>
     * 优先从请求头中获取追踪 ID；如果不存在、过长或包含非法字符，则自动生成新的 TraceId。
     * </p>
     *
     * @param request HTTP 请求对象
     * @return 可安全写入日志和响应头的 TraceId
     */
    private String extractTraceId(HttpServletRequest request) {
        String headerId = request.getHeader(TRACE_ID_HEADER);
        if (isValidTraceId(headerId)) {
            return headerId;
        }
        return TraceIdContext.generate();
    }

    /**
     * 校验外部传入的 TraceId，避免日志污染或响应头异常。
     *
     * @param traceId 外部传入的 TraceId
     * @return true 表示合法，false 表示需要重新生成
     */
    private boolean isValidTraceId(String traceId) {
        return StringUtils.isNotBlank(traceId)
                && traceId.length() <= MAX_TRACE_ID_LENGTH
                && TRACE_ID_PATTERN.matcher(traceId).matches();
    }
}