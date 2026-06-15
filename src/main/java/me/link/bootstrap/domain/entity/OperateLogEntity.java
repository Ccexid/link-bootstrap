package me.link.bootstrap.domain.entity;

import me.link.bootstrap.domain.valueobject.StatusEnum;

import java.time.LocalDateTime;

/**
 * 操作日志领域实体，记录用户操作、请求信息、业务标识和执行结果。
 */
public class OperateLogEntity {

    private Long id;

    private String traceId;

    private Long userId;

    private Integer userType;

    private String userIp;

    private String userAgent;

    private String module;

    private Integer operation;

    private Long bizId;

    private String action;

    private String extra;

    private Boolean success;

    private String requestMethod;

    private String requestUrl;

    private Integer duration;

    private Long tenantId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private OperateLogEntity(Long id, String traceId, Long userId, Integer userType, String userIp, String userAgent, String module, Integer operation, Long bizId, String action, String extra, Boolean success, String requestMethod, String requestUrl, Integer duration, Long tenantId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.traceId = traceId;
        this.userId = userId;
        this.userType = userType;
        this.userIp = userIp;
        this.userAgent = userAgent;
        this.module = module;
        this.operation = operation;
        this.bizId = bizId;
        this.action = action;
        this.extra = extra;
        this.success = success;
        this.requestMethod = requestMethod;
        this.requestUrl = requestUrl;
        this.duration = duration;
        this.tenantId = tenantId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static OperateLogEntity create(String traceId, Long userId, Integer userType, String userIp, String userAgent, String module, Integer operation, Long bizId, String action, String extra, Boolean success, String requestMethod, String requestUrl, Integer duration, Long tenantId) {
        return new OperateLogEntity(null, traceId, userId, userType, userIp, userAgent, module, operation, bizId, action, extra, success, requestMethod, requestUrl, duration, tenantId, null, null);
    }

    public static OperateLogEntity restore(Long id, String traceId, Long userId, Integer userType, String userIp, String userAgent, String module, Integer operation, Long bizId, String action, String extra, Boolean success, String requestMethod, String requestUrl, Integer duration, Long tenantId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new OperateLogEntity(id, traceId, userId, userType, userIp, userAgent, module, operation, bizId, action, extra, success, requestMethod, requestUrl, duration, tenantId, createdAt, updatedAt);
    }

    public void changeBasicInfo(String traceId, Long userId, Integer userType, String userIp, String userAgent, String module, Integer operation, Long bizId, String action, String extra, Boolean success, String requestMethod, String requestUrl, Integer duration, Long tenantId) {
        this.traceId = traceId;
        this.userId = userId;
        this.userType = userType;
        this.userIp = userIp;
        this.userAgent = userAgent;
        this.module = module;
        this.operation = operation;
        this.bizId = bizId;
        this.action = action;
        this.extra = extra;
        this.success = success;
        this.requestMethod = requestMethod;
        this.requestUrl = requestUrl;
        this.duration = duration;
        this.tenantId = tenantId;
    }


    public Long getId() {
        return id;
    }

    public String getTraceId() {
        return traceId;
    }

    public Long getUserId() {
        return userId;
    }

    public Integer getUserType() {
        return userType;
    }

    public String getUserIp() {
        return userIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getModule() {
        return module;
    }

    public Integer getOperation() {
        return operation;
    }

    public Long getBizId() {
        return bizId;
    }

    public String getAction() {
        return action;
    }

    public String getExtra() {
        return extra;
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public Integer getDuration() {
        return duration;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
