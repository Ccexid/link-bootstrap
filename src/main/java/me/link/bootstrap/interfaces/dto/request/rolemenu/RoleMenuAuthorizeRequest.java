package me.link.bootstrap.interfaces.dto.request.rolemenu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "角色菜单批量授权请求")
public class RoleMenuAuthorizeRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "roleId")
    private Long roleId;

    @Schema(description = "menuIds")
    private List<Long> menuIds;
}
