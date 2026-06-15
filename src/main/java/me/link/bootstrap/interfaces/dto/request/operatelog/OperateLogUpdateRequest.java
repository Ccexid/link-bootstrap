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

    @Schema(description = "traceId")
    private String traceId;
    @Schema(description = "userId")
    private Long userId;
    @Schema(description = "userType")
    private Integer userType;
    @Schema(description = "userIp")
    private String userIp;
    @Schema(description = "userAgent")
    private String userAgent;
    @Schema(description = "module")
    private String module;
    @Schema(description = "operation")
    private Integer operation;
    @Schema(description = "bizId")
    private Long bizId;
    @Schema(description = "action")
    private String action;
    @Schema(description = "extra")
    private String extra;
    @Schema(description = "success")
    private Boolean success;
    @Schema(description = "requestMethod")
    private String requestMethod;
    @Schema(description = "requestUrl")
    private String requestUrl;
    @Schema(description = "duration")
    private Integer duration;
}
