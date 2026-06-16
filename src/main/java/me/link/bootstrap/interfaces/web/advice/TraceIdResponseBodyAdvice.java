package me.link.bootstrap.interfaces.web.advice;

import me.link.bootstrap.interfaces.dto.response.ResultResponse;
import me.link.bootstrap.interfaces.dto.response.ResultTableResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import static me.link.bootstrap.shared.kernel.constant.GlobalConstants.TRACE_ID_HEADER;

/**
 * 链路追踪响应增强器，负责在统一响应体中追加 traceId。
 */
@RestControllerAdvice(basePackages = "me.link.bootstrap.interfaces.controller")
public class TraceIdResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    /**
     * 判断当前响应增强器是否支持该返回类型。
     */
    @Override
    public boolean supports(@NonNull MethodParameter returnType,
            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> parameterType = returnType.getParameterType();
        return ResultResponse.class.isAssignableFrom(parameterType)
                || ResultTableResponse.class.isAssignableFrom(parameterType);
    }

    /**
     * 在响应体写出前补充统一字段。
     */
    @Override
    public Object beforeBodyWrite(@Nullable Object body, @NonNull MethodParameter returnType,
            @NonNull MediaType selectedContentType,
            @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
            @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {
        String traceId = resolveTraceId(request, response);

        if (body instanceof ResultResponse<?> resultResponse) {
            resultResponse.setTraceId(traceId);
        } else if (body instanceof ResultTableResponse<?> resultTableResponse) {
            resultTableResponse.setTraceId(traceId);
        }

        return body;
    }

    private String resolveTraceId(ServerHttpRequest request, ServerHttpResponse response) {
        Object traceId = request instanceof ServletServerHttpRequest servletRequest
                ? servletRequest.getServletRequest().getAttribute(TRACE_ID_HEADER)
                : null;
        if (traceId instanceof String value) {
            return value;
        }

        String responseTraceId = response.getHeaders().getFirst(TRACE_ID_HEADER);
        if (responseTraceId != null) {
            return responseTraceId;
        }

        return request.getHeaders().getFirst(TRACE_ID_HEADER);
    }
}
