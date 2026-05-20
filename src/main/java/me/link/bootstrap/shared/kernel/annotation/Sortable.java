package me.link.bootstrap.shared.kernel.annotation;

import java.lang.annotation.*;

/**
 * 标记字段是否支持排序的注解
 * <p>
 * 用于 DTO/VO 属性上，配合拦截器或切面，自动将可排序字段列表返回给前端。
 * </p>
 *
 * @author Ccexid
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Sortable {
    /**
     * 前端排序时使用的别名（默认与字段名一致）
     * <p>
     * 如果前端需要传下划线格式，而你的字段是驼峰，可以通过此属性指定。
     * 不指定时，全局脚手架会自动将其转为下划线。
     * </p>
     */
    String value() default "";

    /**
     * 该可排序字段的文字描述（例如：创建时间、点击量）
     */
    String description() default "";
}
