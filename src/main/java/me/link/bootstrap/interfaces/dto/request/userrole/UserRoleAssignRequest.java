package me.link.bootstrap.interfaces.dto.request.userrole;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 用户角色批量分配请求 DTO，承载用户与角色集合的分配关系。
 */
@Data
@Schema(description = "用户角色批量分配请求")
public class UserRoleAssignRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户编号")
    private Long userId;

    @Schema(description = "角色编号数组")
    private List<Long> roleIds;
}
