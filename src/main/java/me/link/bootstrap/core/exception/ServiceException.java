package me.link.bootstrap.core.exception;


import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {
    private final long code;

    public ServiceException(IErrorCode errorCode) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
    }

    public ServiceException(String message) {
        super(message);
        this.code = ErrorCode.OPERATION_FAILED.getCode();
    }
}
