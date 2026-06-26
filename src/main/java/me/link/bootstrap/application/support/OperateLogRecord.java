package me.link.bootstrap.application.support;

import lombok.Data;

/**
 * 应用内部操作日志记录载体，不作为 HTTP 入参暴露。
 */
@Data
public class OperateLogRecord {

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
}
