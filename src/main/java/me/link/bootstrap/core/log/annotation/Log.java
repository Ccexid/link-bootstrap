package me.link.bootstrap.core.log.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    /** 模块名称 */
    String module() default "";

    /** 操作描述 (支持 SpEL) */
    String operation() default "";

    /** 业务 ID (支持 SpEL) */
    String businessId() default "";

    /** 查询数据的 Service Bean 名称 (用于执行前后的数据对比) */
    String serviceName() default "";

    /** * 是否记录详细字段变更 (Diff)
     * 【新增这个字段即可解决报错】
     */
    boolean isDiff() default true;
}