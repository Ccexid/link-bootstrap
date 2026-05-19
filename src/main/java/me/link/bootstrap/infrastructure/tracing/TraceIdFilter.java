package me.link.bootstrap.infrastructure.tracing;

import cn.hutool.core.util.ObjectUtil;
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

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceIdFilter extends OncePerRequestFilter {
    /**
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String traceId = extractTraceId(request);
            TraceIdContext.init(traceId);
            filterChain.doFilter(request, response);
        } finally {
            TraceIdContext.clear();
        }
    }

    /**
     * 从HTTP请求中提取或生成TraceId
     * <p>
     * 优先从请求头"X-Trace-Id"中获取追踪ID，如果不存在或为空，
     * 则自动生成一个新的TraceId
     *
     * @param request HTTP请求对象，用于获取请求头中的TraceId
     * @return 提取到的TraceId或新生成的TraceId字符串
     */
    private String extractTraceId(HttpServletRequest request) {
        String headerId = request.getHeader("X-Trace-Id");
        return StringUtils.isAllBlank(headerId) ? TraceIdContext.generate() : headerId;
    }
}
