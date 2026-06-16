package me.link.bootstrap.interfaces.dto.request.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import me.link.bootstrap.domain.valueobject.StatusEnum;

import java.io.Serial;
import java.io.Serializable;

/**
 * 创建角色请求 DTO，承载角色基础信息、权限编码和数据权限配置。
 */
@Data
@Schema(description = "创建角色请求")
public class RoleCreateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "租户管理员")
    @NotBlank(message = "角色名称不能为空")
    private String name;

    @Schema(description = "角色权限编码(同租户内唯一,用于角色权限校验)",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "tenant_admin")
    @NotBlank(message = "角色权限编码不能为空")
    private String code;

    @Schema(description = "显示顺序(数值越小越靠前)", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "显示顺序不能为空")
    private Integer sort;

    @Schema(description = "数据范围:1全部 2自定义 3本组织 4本组织及以下", example = "1")
    private Integer dataScope;

    @Schema(description = "数据范围(指定组织或部门编号数组,逗号分隔)")
    private String dataScopeDeptIds;

    @Schema(description = "角色状态(0正常 1停用)", example = "0")
    private StatusEnum status;

    @Schema(description = "角色类型:1系统内置 2自定义", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "角色类型不能为空")
    private Integer type;

    @Schema(description = "备注")
    private String remark;
}
