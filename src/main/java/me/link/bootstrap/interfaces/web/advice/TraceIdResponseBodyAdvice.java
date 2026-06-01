package me.link.bootstrap.interfaces.web.advice;

import me.link.bootstrap.interfaces.dto.response.ResultResponse;
import me.link.bootstrap.interfaces.dto.response.ResultTableResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import static me.link.bootstrap.shared.kernel.constant.GlobalConstants.TRACE_ID_HEADER;

@RestControllerAdvice(basePackages = "me.link.bootstrap.interfaces.controller")
public class TraceIdResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> parameterType = returnType.getParameterType();
        return ResultResponse.class.isAssignableFrom(parameterType)
                || ResultTableResponse.class.isAssignableFrom(parameterType);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
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
