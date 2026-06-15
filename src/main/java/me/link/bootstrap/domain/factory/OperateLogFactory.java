package me.link.bootstrap.domain.factory;

import me.link.bootstrap.domain.entity.OperateLogEntity;
import me.link.bootstrap.domain.valueobject.StatusEnum;

import java.time.LocalDateTime;

/**
 * 操作日志领域工厂，集中封装操作日志创建和变更时的业务规则校验。
 */
public final class OperateLogFactory {

    private OperateLogFactory() {
        throw new UnsupportedOperationException("工厂类不允许实例化");
    }

    public static OperateLogEntity create(String traceId, Long userId, Integer userType, String userIp, String userAgent, String module, Integer operation, Long bizId, String action, String extra, Boolean success, String requestMethod, String requestUrl, Integer duration, Long tenantId) {
        validate(traceId, userId, userType, userIp, userAgent, module, operation, bizId, action, extra, success, requestMethod, requestUrl, duration, tenantId);
        return OperateLogEntity.create(traceId, userId, userType, userIp, userAgent, module, operation, bizId, action, extra, success, requestMethod, requestUrl, duration, tenantId);
    }

    public static void changeBasicInfo(OperateLogEntity operateLog, String traceId, Long userId, Integer userType, String userIp, String userAgent, String module, Integer operation, Long bizId, String action, String extra, Boolean success, String requestMethod, String requestUrl, Integer duration, Long tenantId) {
        if (operateLog == null) {
            throw new IllegalArgumentException("操作日志不能为空");
        }
        validate(traceId, userId, userType, userIp, userAgent, module, operation, bizId, action, extra, success, requestMethod, requestUrl, duration, tenantId);
        operateLog.changeBasicInfo(traceId, userId, userType, userIp, userAgent, module, operation, bizId, action, extra, success, requestMethod, requestUrl, duration, tenantId);
    }

    private static void validate(String traceId, Long userId, Integer userType, String userIp, String userAgent, String module, Integer operation, Long bizId, String action, String extra, Boolean success, String requestMethod, String requestUrl, Integer duration, Long tenantId) {
        if (userId == null || userId < 0) {
            throw new IllegalArgumentException("操作日志userId不能小于0");
        }
        if (module == null || module.trim().isEmpty()) {
            throw new IllegalArgumentException("操作日志module不能为空");
        }
        if (bizId == null || bizId < 0) {
            throw new IllegalArgumentException("操作日志bizId不能小于0");
        }
        if (action == null || action.trim().isEmpty()) {
            throw new IllegalArgumentException("操作日志action不能为空");
        }
    }
}
