package me.link.bootstrap.shared.kernel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 幂等性校验注解，用于防止短时间内重复提交同一个写请求。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {

    /**
     * 幂等 key 过期时间，单位秒。
     */
    long timeoutSeconds() default 5L;

    /**
     * 重复提交时返回的提示信息。
     */
    String message() default "请勿重复提交";

    /**
     * 客户端显式幂等 key 请求头。为空时自动使用请求路径和参数生成指纹。
     */
    String headerName() default "Idempotency-Key";
}
