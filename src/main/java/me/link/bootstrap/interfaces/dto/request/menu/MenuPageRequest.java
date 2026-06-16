package me.link.bootstrap.interfaces.dto.request.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import me.link.bootstrap.domain.valueobject.StatusEnum;
import me.link.bootstrap.interfaces.dto.request.SortablePageRequest;

import java.io.Serial;

/**
 * 菜单分页查询请求 DTO，承载菜单筛选条件、分页参数和排序字段。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "菜单分页查询请求")
public class MenuPageRequest extends SortablePageRequest {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "权限标识")
    private String permission;

    @Schema(description = "类型")
    private Integer type;

    @Schema(description = "父级编号")
    private Long parentId;

    @Schema(description = "状态")
    private StatusEnum status;
}
