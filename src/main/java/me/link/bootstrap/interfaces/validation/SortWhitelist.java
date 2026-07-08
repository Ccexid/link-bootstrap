package me.link.bootstrap.interfaces.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 排序字段白名单校验注解，用于限制分页请求只能按指定 VO 的可排序字段排序。
 */
@Documented
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SortWhitelistValidator.class)
public @interface SortWhitelist {

    /**
     * 返回注解配置的字段集合。
     */
    Class<?> value();

    /**
     * 返回校验失败时的提示消息。
     */
    String message() default "排序字段不在允许范围内";

    /**
     * 返回 Bean Validation 分组。
     */
    Class<?>[] groups() default {};

    /**
     * 返回 Bean Validation 负载类型。
     */
    Class<? extends Payload>[] payload() default {};
}
