package me.link.bootstrap.application.command;

/**
 * 创建操作日志命令对象，封装记录操作日志所需的数据。
 * <p>
 * 租户ID将从当前登录用户的上下文中自动获取，无需前端传入。
 * </p>
 */
public record CreateOperateLogCommand(String traceId, Long userId, Integer userType, String userIp, String userAgent, String module, Integer operation, Long bizId, String action, String extra, Boolean success, String requestMethod, String requestUrl, Integer duration) {
}
