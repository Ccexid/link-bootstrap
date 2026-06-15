package me.link.bootstrap.interfaces.dto.request.userrole;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

/**
 * 创建用户角色关联请求 DTO，承载单条用户与角色绑定关系。
 */
@Data
@Schema(description = "创建用户角色关联请求")
public class UserRoleCreateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "userId")
    private Long userId;
    @Schema(description = "roleId")
    private Long roleId;
}
