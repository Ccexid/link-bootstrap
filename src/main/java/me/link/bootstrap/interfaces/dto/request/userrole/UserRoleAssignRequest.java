package me.link.bootstrap.interfaces.dto.request.userrole;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "用户角色批量分配请求")
public class UserRoleAssignRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "userId")
    private Long userId;

    @Schema(description = "roleIds")
    private List<Long> roleIds;

    @Schema(description = "tenantId")
    private Long tenantId;
}
