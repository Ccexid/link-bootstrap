package me.link.bootstrap.interfaces.dto.request.userrole;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

/**
 * 更新用户角色关联请求 DTO，承载用户与角色绑定关系的变更值。
 */
@Data
@Schema(description = "更新用户角色关联请求")
public class UserRoleUpdateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户编号")
    private Long userId;
    @Schema(description = "角色编号")
    private Long roleId;
}
