package me.link.bootstrap.shared.kernel.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一业务错误码枚举
 * <p>
 * 定义全局业务错误码规范,采用分段编码策略:
 * - 0: 成功标识
 * - 4xx_xxx_xxx: 客户端错误(参数校验、权限不足等)
 * - 5xx_xxx_xxx: 服务端/业务错误(用户异常、系统异常等)
 * </p>
 * <p>
 * 错误码格式: {HTTP状态码段}_{模块编号}_{具体错误编号}
 * 示例: 500_001_001 表示 500段-用户模块-第1个错误
 * </p>
 *
 * @author Ccexid
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {
    /** 操作成功 */
    SUCCESS(0, "操作成功"),

    /** 客户端错误: 请求参数未通过校验 */
    PARAM_VALIDATION_ERROR(400_000_001, "参数校验失败"),
    /** 客户端错误: 未提供有效认证令牌或令牌已失效 */
    UNAUTHORIZED(401_000_001, "未登录或登录已失效"),
    /** 客户端错误: 已认证但无权访问目标资源 */
    FORBIDDEN(403_000_001, "无权访问该资源"),
    /** 客户端错误: 请求的接口或资源不存在 */
    NOT_FOUND(404_000_001, "请求的资源不存在"),
    /** 客户端错误: HTTP请求方法不被允许 */
    METHOD_NOT_ALLOWED(405_000_001, "请求方法不支持"),

    /** 业务错误: 根据用户名或ID未查询到用户记录 */
    USER_NOT_FOUND(500_001_001, "用户不存在"),
    /** 业务错误: 用户登录时密码校验失败 */
    USER_PASSWORD_ERROR(500_001_002, "密码错误"),
    /** 业务错误: 用户账户状态为禁用,不允许登录或操作 */
    USER_DISABLED(500_001_003, "用户已被禁用"),
    /** 业务错误: 租户订阅套餐已过期,限制部分或全部功能 */
    TENANT_EXPIRED(500_002_001, "租户已过期"),
    /** 业务错误: 系统内部发生未预期异常,需查看日志定位 */
    SYSTEM_ERROR(500_999_999, "系统内部错误");

    /** 业务错误码数值,用于前端识别具体错误类型 */
    private final long code;
    /** 错误描述信息,用于向用户展示或日志记录 */
    private final String message;
}
