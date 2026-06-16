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

    @Schema(description = "名称")
    private String name;
    @Schema(description = "编码")
    private String code;
    @Schema(description = "显示顺序")
    private Integer sort;
    @Schema(description = "数据范围")
    private Integer dataScope;
    @Schema(description = "数据范围组织编号数组")
    private String dataScopeDeptIds;
    @Schema(description = "状态")
    private StatusEnum status;
    @Schema(description = "类型")
    private Integer type;
    @Schema(description = "备注")
    private String remark;
}
