package me.link.bootstrap.interfaces.dto.response.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import me.link.bootstrap.domain.valueobject.StatusEnum;
import me.link.bootstrap.shared.kernel.annotation.Sortable;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "操作日志信息")
public class OperateLogResponseVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    @Sortable(description = "id")
    private Long id;
    @Schema(description = "traceId")
    private String traceId;
    @Schema(description = "userId")
    @Sortable(description = "userId")
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
    @Sortable(description = "bizId")
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
    @Schema(description = "tenantId")
    @Sortable(description = "tenantId")
    private Long tenantId;
    @Schema(description = "createdAt")
    @Sortable(description = "createdAt")
    private LocalDateTime createdAt;
    @Schema(description = "updatedAt")
    @Sortable(description = "updatedAt")
    private LocalDateTime updatedAt;
}
