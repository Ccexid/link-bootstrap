package me.link.bootstrap.core.exception;

/**
 * 错误码接口规范
 */
public interface IErrorCode {
    long getCode();
    String getMsg();
}