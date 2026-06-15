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

    public static <T> T requireFound(Optional<T> optional, ErrorCode errorCode) {
        return optional.orElseThrow(() -> new BusinessException(errorCode));
    }

    public static void requireSuccess(boolean success, ErrorCode errorCode) {
        if (!success) {
            throw new BusinessException(errorCode);
        }
    }
}
