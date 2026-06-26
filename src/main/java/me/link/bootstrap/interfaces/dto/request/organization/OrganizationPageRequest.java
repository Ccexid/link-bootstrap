package me.link.bootstrap.interfaces.dto.request.organization;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import me.link.bootstrap.shared.kernel.valueobject.StatusEnum;
import me.link.bootstrap.interfaces.dto.request.SortablePageRequest;

import java.io.Serial;

/**
 * 组织分页查询请求 DTO，承载组织筛选条件、分页参数和排序字段。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "组织分页查询请求")
public class OrganizationPageRequest extends SortablePageRequest {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "组织类型")
    private Integer orgType;

    @Schema(description = "父级编号")
    private Long parentId;

    @Schema(description = "状态")
    private StatusEnum status;
}
