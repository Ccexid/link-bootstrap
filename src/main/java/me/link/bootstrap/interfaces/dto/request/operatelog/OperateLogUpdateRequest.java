package me.link.bootstrap.interfaces.dto.request.operatelog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

/**
 * 更新操作日志请求 DTO，承载审计日志记录的变更值。
 */
@Data
@Schema(description = "更新操作日志请求")
public class OperateLogUpdateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "链路追踪编号")
    private String traceId;
    @Schema(description = "用户编号")
    private Long userId;
    @Schema(description = "用户类型")
    private Integer userType;
    @Schema(description = "用户地址")
    private String userIp;
    @Schema(description = "用户代理")
    private String userAgent;
    @Schema(description = "模块")
    private String module;
    @Schema(description = "操作类型")
    private Integer operation;
    @Schema(description = "业务编号")
    private Long bizId;
    @Schema(description = "操作动作")
    private String action;
    @Schema(description = "扩展信息")
    private String extra;
    @Schema(description = "是否成功")
    private Boolean success;
    @Schema(description = "请求方法")
    private String requestMethod;
    @Schema(description = "请求地址")
    private String requestUrl;
    @Schema(description = "耗时")
    private Integer duration;
}
