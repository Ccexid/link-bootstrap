package me.link.bootstrap.interfaces.dto.request.operatelog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import me.link.bootstrap.interfaces.dto.request.SortablePageRequest;

import java.io.Serial;

/**
 * 操作日志分页查询请求 DTO，承载审计日志筛选条件、分页参数和排序字段。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "操作日志分页查询请求")
public class OperateLogPageRequest extends SortablePageRequest {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "链路追踪编号")
    private String traceId;

    @Schema(description = "用户编号")
    private Long userId;

    @Schema(description = "模块")
    private String module;

    @Schema(description = "操作类型")
    private Integer operation;

    @Schema(description = "业务编号")
    private Long bizId;

    @Schema(description = "是否成功")
    private Boolean success;
}
