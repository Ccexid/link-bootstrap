package me.link.bootstrap.infrastructure.annotation;

import me.link.bootstrap.infrastructure.enums.LogType;
import java.lang.annotation.*;

/**
 * 操作日志记录注解
 * <p>
 * 放置在基础设施层，因为它属于横切关注点（Cross-cutting Concerns），
 * 不属于具体的业务领域逻辑，但服务于整个领域。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /**
     * 模块名称
     * 示例：@Log(module = "用户中心")
     */
    String module() default "";

    /**
     * 操作描述 (支持 SpEL)
     * 示例：@Log(operation = "'修改用户: ' + #user.nickname")
     */
    String operation() default "";

    /**
     * 业务对象 ID (支持 SpEL)
     * 用于快速定位受影响的业务数据主键
     * 示例：@Log(businessId = "#dto.id")
     */
    String businessId() default "";

    /**
     * 操作类型
     * 用于后台日志系统的分类检索
     */
    LogType type() default LogType.OTHER;

    /**
     * 查询数据的 Service Bean 名称
     * 用于在执行业务前获取“旧数据”，实现数据变更对比 (Diff)
     */
    String serviceName() default "";

    /**
     * 是否记录详细字段变更 (Diff)
     * 开启后，系统将尝试对比执行前后的对象差异
     */
    boolean isDiff() default false;

    /**
     * 是否保存请求参数
     * 对于上传文件或包含大文本参数的接口，建议设为 false
     */
    boolean saveRequestParam() default true;

    /**
     * 是否保存响应结果
     * 敏感信息接口建议设为 false
     */
    boolean saveResponseData() default true;

    /**
     * 排除指定的请求参数名
     * 示例：{"password", "oldPassword"}
     */
    String[] excludeParamNames() default {};
}