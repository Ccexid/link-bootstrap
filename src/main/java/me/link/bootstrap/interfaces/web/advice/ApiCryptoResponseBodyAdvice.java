package me.link.bootstrap.interfaces.web.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.link.bootstrap.infrastructure.crypto.ApiCryptoService;
import me.link.bootstrap.interfaces.dto.response.ResultResponse;
import me.link.bootstrap.interfaces.dto.response.ResultTableResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Map;

/**
 * 将统一响应对象序列化后进行 RSA 加密。
 */
@Order()
@ConditionalOnProperty(prefix = "link.api-crypto", name = "enabled", havingValue = "true")
@RestControllerAdvice(basePackages = "me.link.bootstrap.interfaces.controller")
public class ApiCryptoResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final ApiCryptoService apiCryptoService;
    private final ObjectMapper objectMapper;

    public ApiCryptoResponseBodyAdvice(ApiCryptoService apiCryptoService, ObjectMapper objectMapper) {
        this.apiCryptoService = apiCryptoService;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(@NonNull MethodParameter returnType,
            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> parameterType = returnType.getParameterType();
        return ResultResponse.class.isAssignableFrom(parameterType)
                || ResultTableResponse.class.isAssignableFrom(parameterType);
    }

    @Override
    public Object beforeBodyWrite(@Nullable Object body, @NonNull MethodParameter returnType,
            @NonNull MediaType selectedContentType,
            @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
            @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {
        if (body == null || !MediaType.APPLICATION_JSON.isCompatibleWith(selectedContentType)) {
            return body;
        }

        String path = request instanceof ServletServerHttpRequest servletRequest
                ? servletRequest.getServletRequest().getRequestURI()
                : request.getURI().getPath();
        if (!apiCryptoService.properties().matches(path)) {
            return body;
        }

        try {
            String encryptedText = apiCryptoService.encryptResponse(objectMapper.writeValueAsString(body));
            response.getHeaders().add("X-Encrypted", "RSA");
            return Map.of(apiCryptoService.properties().getResponseField(), encryptedText);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("响应加密失败", e);
        }
    }
}
