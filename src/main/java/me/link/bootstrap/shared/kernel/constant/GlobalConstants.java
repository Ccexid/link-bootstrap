package me.link.bootstrap.shared.kernel.constant;

/**
 * 全局常量定义
 * <p>
 * 包含系统级别的全局常量，涵盖 API 版本、前缀以及链路追踪等相关配置。
 * </p>
 *
 * @author Ccexid
 */
public interface GlobalConstants {

    /** API 版本号 */
    String API_VERSION = "v1";

    /** API 基础路径前缀 (例如: /api/v1) */
    String API_PREFIX = "/api/" + API_VERSION;

    /** TraceId 请求头名称，用于分布式链路追踪透传 */
    String TRACE_ID_HEADER = "X-Trace-Id";
}
