package me.link.bootstrap.interfaces.dto.request.rolemenu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(description = "创建角色菜单关联请求")
public class RoleMenuCreateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "roleId")
    private Long roleId;
    @Schema(description = "menuId")
    private Long menuId;
    @Schema(description = "tenantId")
    private Long tenantId;
}
