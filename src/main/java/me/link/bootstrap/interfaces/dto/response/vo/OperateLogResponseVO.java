package me.link.bootstrap.interfaces.dto.response.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import me.link.bootstrap.domain.valueobject.StatusEnum;
import me.link.bootstrap.shared.kernel.annotation.Sortable;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志响应 VO，定义接口返回给前端的审计日志字段。
 */
@Data
@Schema(description = "操作日志信息")
public class OperateLogResponseVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "编号")
    @Sortable(description = "id")
    private Long id;
    @Schema(description = "链路追踪编号")
    private String traceId;
    @Schema(description = "用户编号")
    @Sortable(description = "userId")
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
    @Sortable(description = "bizId")
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
    @Schema(description = "租户编号")
    @Sortable(description = "tenantId")
    private Long tenantId;
    @Schema(description = "创建时间")
    @Sortable(description = "createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @Schema(description = "更新时间")
    @Sortable(description = "updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
