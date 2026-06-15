package me.link.bootstrap.interfaces.dto.request.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import me.link.bootstrap.domain.valueobject.StatusEnum;

import java.time.LocalDateTime;
import java.io.Serial;
import java.io.Serializable;

/**
 * 更新角色请求 DTO，承载角色基础信息、权限编码和数据权限配置的变更值。
 */
@Data
@Schema(description = "更新角色请求")
public class RoleUpdateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "name")
    private String name;
    @Schema(description = "code")
    private String code;
    @Schema(description = "sort")
    private Integer sort;
    @Schema(description = "dataScope")
    private Integer dataScope;
    @Schema(description = "dataScopeDeptIds")
    private String dataScopeDeptIds;
    @Schema(description = "status")
    private StatusEnum status;
    @Schema(description = "type")
    private Integer type;
    @Schema(description = "remark")
    private String remark;
}
