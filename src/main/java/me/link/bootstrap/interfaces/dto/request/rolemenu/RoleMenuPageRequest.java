package me.link.bootstrap.interfaces.dto.request.rolemenu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import me.link.bootstrap.interfaces.dto.request.SortablePageRequest;

import java.io.Serial;

/**
 * 角色菜单关联分页查询请求 DTO，承载角色菜单关系的筛选、分页和排序条件。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "角色菜单关联分页查询请求")
public class RoleMenuPageRequest extends SortablePageRequest {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "角色编号")
    private Long roleId;

    @Schema(description = "菜单编号")
    private Long menuId;
}
