package me.link.bootstrap.core.model;

import me.link.bootstrap.core.exception.ErrorCode;
import me.link.bootstrap.core.exception.IErrorCode;
import me.link.bootstrap.core.utils.ClockUtil;

import java.io.Serializable;

/**
 * 通用响应结果封装类 (基于 Java Record)
 * 优化点：
 * 1. 适配 ClockUtil 规范。
 * 2. 增加 isSuccess() 逻辑判断方法。
 * 3. 语义化方法名调整（error -> failed）。
 * 4. 增加静态空数据成功方法。
 */
public record Result<T>(
        long code,
        String msg,
        T data,
        long timestamp
) implements Serializable {

    /**
     * 紧凑型构造函数
     * 自动填充时间戳，确保数据一致性
     */
    public Result {
        if (timestamp <= 0) {
            timestamp = ClockUtil.now();
        }
    }

    // ========== 静态工厂方法 (Success) ==========

    /**
     * 操作成功 (带数据)
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMsg(), data, 0);
    }

    /**
     * 操作成功 (不带数据)
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    // ========== 静态工厂方法 (Failed) ==========

    /**
     * 操作失败 (基于枚举)
     */
    public static <T> Result<T> failed(IErrorCode errorCode) {
        return new Result<>(errorCode.getCode(), errorCode.getMsg(), null, 0);
    }

    /**
     * 操作失败 (自定义消息，基于枚举码)
     */
    public static <T> Result<T> failed(IErrorCode errorCode, String customMsg) {
        return new Result<>(errorCode.getCode(), customMsg, null, 0);
    }

    /**
     * 操作失败 (完全自定义)
     */
    public static <T> Result<T> failed(long code, String msg) {
        return new Result<>(code, msg, null, 0);
    }

    // ========== 业务增强方法 ==========

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return this.code == ErrorCode.SUCCESS.getCode();
    }
}