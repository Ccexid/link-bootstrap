package me.link.sbc.common.api;

import lombok.Getter;

/**
 * 枚举类，用于定义系统中常见的响应结果码及其对应的描述信息。
 * 每个枚举常量包含一个状态码（code）和一条消息（message），用于统一处理接口返回结果。
 */
@Getter
public enum ResultCode {
    /**
     * 操作成功的响应码。
     * code: 200
     * message: "操作成功"
     */
    SUCCESS(200, "操作成功"),

    /**
     * 业务异常的响应码。
     * code: 500
     * message: "业务异常"
     */
    FAILURE(500, "业务异常"),

    /**
     * 未认证或Token已过期的响应码。
     * code: 401
     * message: "未认证或Token已过期"
     */
    UNAUTHORIZED(401, "未认证或Token已过期"),

    /**
     * 没有相关权限的响应码。
     * code: 403
     * message: "没有相关权限"
     */
    FORBIDDEN(403, "没有相关权限"),

    /**
     * 参数校验失败的响应码。
     * code: 400
     * message: "参数校验失败"
     */
    PARAM_ERROR(400, "参数校验失败");

    /**
     * 响应状态码。
     */
    private final int code;

    /**
     * 响应消息描述。
     */
    private final String message;

    /**
     * 构造方法，初始化枚举常量的状态码和消息。
     *
     * @param code    响应状态码
     * @param message 响应消息描述
     */
    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
