package me.link.bootstrap.interfaces.dto.request.rolemenu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

/**
 * 创建角色菜单关联请求 DTO，承载单条角色与菜单绑定关系。
 */
@Data
@Schema(description = "创建角色菜单关联请求")
public class RoleMenuCreateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "角色编号")
    private Long roleId;
    @Schema(description = "菜单编号")
    private Long menuId;
}
