package me.link.bootstrap.core.exception;

/**
 * 错误码接口规范
 * 所有的错误码枚举都必须实现此接口，以便全局异常处理器统一识别
 */
public interface IErrorCode {

    /**
     * 获取错误码
     */
    long getCode();

    /**
     * 获取错误描述
     */
    String getMsg();

    /**
     * 快捷方法：直接将当前错误码封装并抛出业务异常
     */
    default void throwExp() {
        throw new ServiceException(this);
    }

    /**
     * 快捷方法：如果条件不成立则抛出当前错误码异常
     * @param condition 成立条件
     */
    default void assertThat(boolean condition) {
        if (!condition) {
            throwExp();
        }
    }

    /**
     * 快捷方法：如果对象为空则抛出当前错误码异常
     */
    default void assertNotNull(Object obj) {
        if (obj == null) {
            throwExp();
        }
    }
}