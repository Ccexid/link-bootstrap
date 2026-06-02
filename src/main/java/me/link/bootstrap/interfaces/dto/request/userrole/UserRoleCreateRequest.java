package me.link.bootstrap.interfaces.dto.request.userrole;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

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
