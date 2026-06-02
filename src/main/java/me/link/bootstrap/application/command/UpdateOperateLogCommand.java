package me.link.bootstrap.application.command;

import java.time.LocalDateTime;

public record UpdateOperateLogCommand(Long id, String traceId, Long userId, Integer userType, String userIp, String userAgent, String module, Integer operation, Long bizId, String action, String extra, Boolean success, String requestMethod, String requestUrl, Integer duration, Long tenantId) {
}
