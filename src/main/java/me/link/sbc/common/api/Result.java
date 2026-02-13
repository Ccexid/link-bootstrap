package me.link.sbc.common.api;

import java.io.Serializable;

/**
 * 统一响应体 (Java 17 Record 版)
 */
public record Result<T>(
        int code,
        String msg,
        T data,
        long timestamp
) implements Serializable {

    // 快捷成功返回（无数据）
    public static <T> Result<T> success() {
        return success(null);
    }

    // 快捷成功返回（含数据）
    public static <T> Result<T> success(T data) {
        return new Result<>(
                ResultCode.SUCCESS.getCode(),
                ResultCode.SUCCESS.getMessage(),
                data,
                System.currentTimeMillis()
        );
    }

    // 快捷失败返回
    public static <T> Result<T> fail(String message) {
        return new Result<>(
                ResultCode.FAILURE.getCode(),
                message,
                null,
                System.currentTimeMillis()
        );
    }

    // 自定义异常返回
    public static <T> Result<T> fail(ResultCode resultCode) {
        return new Result<>(
                resultCode.getCode(),
                resultCode.getMessage(),
                null,
                System.currentTimeMillis()
        );
    }
}
