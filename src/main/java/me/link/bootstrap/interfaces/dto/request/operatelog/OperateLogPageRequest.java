package me.link.bootstrap.interfaces.dto.request.operatelog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import me.link.bootstrap.domain.valueobject.StatusEnum;
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

    @Schema(description = "traceId")
    private String traceId;

    @Schema(description = "userId")
    private Long userId;

    @Schema(description = "module")
    private String module;

    @Schema(description = "operation")
    private Integer operation;

    @Schema(description = "bizId")
    private Long bizId;

    @Schema(description = "success")
    private Boolean success;
}
