package me.link.bootstrap.interfaces.dto.request.rolemenu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

/**
 * 更新角色菜单关联请求 DTO，承载角色与菜单绑定关系的变更值。
 */
@Data
@Schema(description = "更新角色菜单关联请求")
public class RoleMenuUpdateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "roleId")
    private Long roleId;
    @Schema(description = "menuId")
    private Long menuId;
}
