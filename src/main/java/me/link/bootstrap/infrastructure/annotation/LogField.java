package me.link.bootstrap.infrastructure.annotation;

import java.lang.annotation.*;

/**
 * 审计日志字段标记注解
 * <p>
 * 作用：标记在 Entity 或 DTO 的字段上，用于数据 Diff 对比时提供字段名、格式化逻辑及脱敏策略。
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogField {

    /**
     * 字段名称（展示用）
     * 示例：@LogField("用户姓名")
     */
    String value() default "";

    /**
     * 日期格式化模板
     * 示例：@LogField(value = "创建时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
     */
    String dateFormat() default "";

    /**
     * 是否是敏感字段（开启后，变更内容将以 * 代替）
     * 示例：@LogField(value = "登录密码", isSensitive = true)
     */
    boolean isSensitive() default false;

    /**
     * 字典翻译类型
     * 示例：性别字段存的是 1/2，设置 dictType = "sys_user_sex" 后，日志将显示 "男/女"
     */
    String dictType() default "";

    /**
     * 是否作为业务主键（在 Diff 列表中优先展示）
     */
    boolean isId() default false;
}