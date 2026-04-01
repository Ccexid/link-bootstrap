package me.link.bootstrap.core.constants;

/**
 * 全局 API 常量类
 * <p>
 * 定义了系统中与 API 相关的全局常量，包括版本号、前缀和日期格式等
 * </p>
 */
public interface GlobalApiConstants {
    /**
     * API 版本号
     */
    String API_VERSION = "v1";

    /**
     * API 请求前缀路径
     * <p>
     * 由固定的 "/api/" 和版本号组成，用于统一 API 路由管理
     * </p>
     */
    String API_PREFIX = "/api/" + API_VERSION;

    /**
     * 日期时间格式化器 - 年月日格式
     * <p>
     * 格式示例：2024-01-15
     * </p>
     */
    String FORMAT_YEAR_MONTH_DAY = "yyyy-MM-dd";

    /**
     * 日期时间格式化器 - 完整时间格式
     * <p>
     * 格式示例：2024-01-15 10:30:45
     * </p>
     */
    String FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND = "yyyy-MM-dd HH:mm:ss";
}
