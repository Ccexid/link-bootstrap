package me.link.bootstrap.interfaces.web.advice;

import me.link.bootstrap.interfaces.dto.response.ResultTableResponse;
import me.link.bootstrap.shared.kernel.util.SortableFieldUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 排序字段响应增强器，自动向表格响应中追加当前 VO 支持的排序字段。
 */
@RestControllerAdvice(basePackages = "me.link.bootstrap.interfaces.controller")
public class SortableFieldsResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final Map<Class<?>, List<String>> sortableCache = new ConcurrentHashMap<>();

    /**
     * 判断当前响应增强器是否支持该返回类型。
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return ResultTableResponse.class.isAssignableFrom(returnType.getParameterType());
    }

    /**
     * 在响应体写出前补充统一字段。
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (!(body instanceof ResultTableResponse<?> resultResponse) || resultResponse.getRecords() == null) {
            return body;
        }

        Class<?> targetClass = extractTargetClass(resultResponse.getRecords(), returnType);

        if (targetClass != null) {
            List<String> sortableFields = sortableCache.computeIfAbsent(targetClass, SortableFieldUtils::parseSortableFields);
            if (!sortableFields.isEmpty()) {
                resultResponse.setSortableFields(sortableFields);
            }
        }

        return resultResponse;
    }

    private Class<?> extractTargetClass(Object records, MethodParameter returnType) {
        if (records instanceof Collection<?> collection && !collection.isEmpty()) {
            Object firstElement = collection.iterator().next();
            if (firstElement != null) {
                return firstElement.getClass();
            }
        }

        Type genericType = returnType.getGenericParameterType();
        if (!(genericType instanceof ParameterizedType parameterizedType)) {
            return null;
        }

        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        if (actualTypeArguments.length == 0) {
            return null;
        }

        Type firstTypeArg = actualTypeArguments[0];

        if (firstTypeArg instanceof Class<?> clazz) {
            return clazz;
        }

        if (firstTypeArg instanceof ParameterizedType collectionType) {
            Type[] collectionTypeArgs = collectionType.getActualTypeArguments();
            if (collectionTypeArgs.length > 0 && collectionTypeArgs[0] instanceof Class<?> clazz) {
                return clazz;
            }
        }

        return null;
    }
}
