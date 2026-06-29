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
     * <p>表达式结果可能来自账号、邮箱等敏感字段，切面会先摘要化再拼接 Redis key。</p>
     */
    String key() default "";

    /**
     * 使用自定义业务 key 时，是否同时纳入客户端 IP。
     * <p>公开登录、验证码等接口建议开启，避免单个账号维度和单个 IP 维度互相绕过。</p>
     */
    boolean includeClientIp() default false;

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
