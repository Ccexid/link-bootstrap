package me.link.bootstrap.infrastructure.web.advice;

import me.link.bootstrap.interfaces.dto.response.ResultTableResponse;
import me.link.bootstrap.shared.kernel.util.SortableFieldUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局表格响应切面：动态注入可排序字段白名单
 * <p>
 * 精准拦截 {@link ResultTableResponse} 响应，自动扫描其内部数据实体的 {@link Sortable} 注解，
 * 统一将支持排序的下划线字段集动态回填。
 * </p>
 *
 * @author 7Link
 */
@RestControllerAdvice(basePackages = "me.link.bootstrap.interfaces.controller")
public class SortableFieldsResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    /**
     * 线程安全的本地解析结果缓存，避免并发请求下重复反射计算
     * 当类没有一个字段带 @Sortable 时，会缓存一个 Collections.emptyList() 占位，
     * 从而彻底免去下一次请求的反射判定。
     */
    private final Map<Class<?>, List<String>> sortableCache = new ConcurrentHashMap<>();

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 💡 优化点 1：精准打击。只拦截 ResultTableResponse 及其子类。
        // 让普通的 ResultResponse 响应走绿色通道直接通过，不在 beforeBodyWrite 中做无意义的判定。
        return ResultTableResponse.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        // 经过 supports 的过滤，这里可以安全转型。若 body 为空则不予处理
        if (!(body instanceof ResultTableResponse<?> resultResponse) || resultResponse.getRecords() == null) {
            return body;
        }

        // 💡 优化点 2：优先基于运行时返回对象的真实类进行判断。
        // 当后端返回复杂多态列表时，具体列表内包装的元素类（如 UserVO）比 MethodParameter 的泛型签名更准。
        Class<?> targetClass = extractTargetClass(resultResponse.getRecords(), returnType);

        if (targetClass != null) {
            // computeIfAbsent 内部是原子操作。由于没有了 hasSortableAnnotation 的高阻断，性能极大提升
            List<String> sortableFields = sortableCache.computeIfAbsent(targetClass, SortableFieldUtils::parseSortableFields);
            if (!sortableFields.isEmpty()) {
                resultResponse.setSortableFields(sortableFields);
            }
        }

        return resultResponse;
    }

    /**
     * 智能提取目标实体类：
     * 优先采用运行时数据的实际承载类型（完美支持动态代理和复杂继承），
     * 只有当数据集合为空（无法推断运行时）时，才回退到基于方法的泛型签名进行编译期推导。
     */
    private Class<?> extractTargetClass(Object records, MethodParameter returnType) {
        // 1. 尝试从真实的运行时数据中揪出元素类型
        if (records instanceof Collection<?> collection && !collection.isEmpty()) {
            Object firstElement = collection.iterator().next();
            if (firstElement != null) {
                return firstElement.getClass();
            }
        }

        // 2. 兜底策略：如果 records 是空集合，通过反射提取 Method 返回值的泛型签名
        Type genericType = returnType.getGenericParameterType();
        if (!(genericType instanceof ParameterizedType parameterizedType)) {
            return null;
        }

        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        if (actualTypeArguments.length == 0) {
            return null;
        }

        Type firstTypeArg = actualTypeArguments[0];

        // 对应 ResultTableResponse<XxxVO> 场景
        if (firstTypeArg instanceof Class<?> clazz) {
            return clazz;
        }

        // 对应 ResultTableResponse<List<XxxVO>> 场景
        if (firstTypeArg instanceof ParameterizedType collectionType) {
            Type[] collectionTypeArgs = collectionType.getActualTypeArguments();
            if (collectionTypeArgs.length > 0 && collectionTypeArgs[0] instanceof Class<?> clazz) {
                return clazz;
            }
        }

        return null;
    }

}