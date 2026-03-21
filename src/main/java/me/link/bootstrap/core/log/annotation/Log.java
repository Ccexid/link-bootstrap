package me.link.bootstrap.core.log.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    /**
     * 模块名称 (如: 订单管理)
     */
    String module() default "";

    /**
     * 操作描述 (如: 修改订单金额)
     */
    String operation() default "";

    /**
     * 业务对象ID的 SpEL 表达式 (如: #orderDTO.orderNo)
     */
    String businessId() default "";

    /**
     * 负责查询旧数据的 Service Bean 名称 (如: orderServiceImpl)
     */
    String serviceName() default "";
}