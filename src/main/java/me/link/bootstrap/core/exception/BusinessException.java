package me.link.bootstrap.core.exception;

import lombok.Getter;

/**
 * 业务异常类，用于封装业务逻辑中发生的错误。
 * 继承自 RuntimeException，支持非受检异常处理机制。
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码：唯一标识具体的业务错误类型。
     * 使用 final 修饰，确保初始化后不可修改。
     */
    private final long code;

    /**
     * 构造函数：基于预定义的错误码接口创建异常实例。
     *
     * @param errorCode 实现 IErrorCode 接口的枚举或类，包含错误码和默认消息
     */
    public BusinessException(IErrorCode errorCode) {
        // 调用父类构造函数，将错误码对应的默认消息作为异常消息
        super(errorCode.getMsg());
        // 从错误码接口中获取具体的错误码数值并赋值给当前类的 code 字段
        this.code = errorCode.getCode();
    }

    /**
     * 构造函数：基于预定义的错误码接口和自定义消息创建异常实例。
     * 适用于需要覆盖默认错误消息的场景。
     *
     * @param errorCode 实现 IErrorCode 接口的枚举或类，提供错误码
     * @param customMsg 用户自定义的详细错误描述信息
     */
    public BusinessException(IErrorCode errorCode, String customMsg) {
        // 调用父类构造函数，使用传入的自定义消息作为异常消息
        super(customMsg);
        // 从错误码接口中获取错误码数值并赋值（即使消息被覆盖，错误码仍保留原定义）
        this.code = errorCode.getCode();
    }

    /**
     * 构造函数：兼容手动输入错误码和消息的场景。
     * 适用于无法使用 IErrorCode 枚举的动态错误处理情况。
     *
     * @param code 手动指定的错误码数值
     * @param msg 手动指定的错误描述信息
     */
    public BusinessException(long code, String msg) {
        // 调用父类构造函数，设置异常消息
        super(msg);
        // 直接赋值手动传入的错误码
        this.code = code;
    }
}