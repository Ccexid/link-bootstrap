package me.link.bootstrap.shared.kernel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 固定窗口限流注解，用于限制接口在指定时间窗口内的访问次数。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /**
     * 限流 key 的 SpEL 表达式。为空时默认按请求路径和客户端 IP 限流。
     */
    String key() default "";

    /**
     * 时间窗口长度，单位秒。
     */
    long windowSeconds() default 60L;

    /**
     * 时间窗口内允许的最大请求次数。
     */
    long maxRequests() default 1L;

    /**
     * 触发限流时返回的提示信息。
     */
    String message() default "操作过于频繁,请稍后再试";
}
