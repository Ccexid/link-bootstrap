package me.link.bootstrap.interfaces.dto.request.organization;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import me.link.bootstrap.domain.valueobject.StatusEnum;

import java.io.Serial;
import java.io.Serializable;

/**
 * 创建组织请求 DTO，承载组织基础信息、层级关系和联系人信息。
 */
@Data
@Schema(description = "创建组织请求")
public class OrganizationCreateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主体名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "示例集团")
    @NotBlank(message = "主体名称不能为空")
    private String name;

    @Schema(description = "主体类型:1供应商 2平台 3商家", requiredMode = Schema.RequiredMode.REQUIRED, example = "3")
    @NotNull(message = "主体类型不能为空")
    private Integer orgType;

    @Schema(description = "上级主体编号(0 代表顶级,商家端通常上级为平台端)", example = "0")
    private Long parentId;

    @Schema(description = "层级路径(用于快速检索,如 0,2,10)", example = "0")
    private String ancestors;

    @Schema(description = "层级深度", example = "1")
    private Integer level;

    @Schema(description = "负责人姓名")
    private String contactName;

    @Schema(description = "完整联系电话(服务端加密、哈希并脱敏后落库)")
    private String contactMobile;

    @Schema(description = "组织状态(0正常 1停用)")
    private StatusEnum status;
}
