package me.link.bootstrap.application.support;

import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;

import java.util.Optional;

/**
 * 应用层常用业务断言。
 */
public final class ApplicationAssert {

    private ApplicationAssert() {
    }

    /**
     * 获取 Optional 中的业务对象，不存在时抛出指定业务错误码。
     */
    public static <T> T requireFound(Optional<T> optional, ErrorCode errorCode) {
        return optional.orElseThrow(() -> new BusinessException(errorCode));
    }

    public static <T> T requireFound(T value, ErrorCode errorCode) {
        if (value == null) {
            throw new BusinessException(errorCode);
        }
        return value;
    }

    /**
     * 校验布尔操作结果，失败时抛出指定业务错误码。
     */
    public static void requireSuccess(boolean success, ErrorCode errorCode) {
        if (!success) {
            throw new BusinessException(errorCode);
        }
    }
}
