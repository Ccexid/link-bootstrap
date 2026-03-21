package me.link.bootstrap.core.common;

import me.link.bootstrap.core.exception.IErrorCode;
import me.link.bootstrap.core.utils.SystemClock;

import java.io.Serializable;

public record Result<T>(
        long code,
        String msg,
        T data,
        long timestamp
) implements Serializable {

    public static <T> Result<T> success(T data) {
        return new Result<>(0, "SUCCESS", data, SystemClock.now());
    }

    public static <T> Result<T> error(IErrorCode errorCode) {
        return new Result<>(errorCode.getCode(), errorCode.getMsg(), null, SystemClock.now());
    }

    public static <T> Result<T> error(long code, String msg) {
        return new Result<>(code, msg, null, SystemClock.now());
    }
}