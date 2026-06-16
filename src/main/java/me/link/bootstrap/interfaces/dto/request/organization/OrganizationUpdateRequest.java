package me.link.bootstrap.interfaces.dto.request.organization;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import me.link.bootstrap.domain.valueobject.StatusEnum;

import java.io.Serial;
import java.io.Serializable;

/**
 * 更新组织请求 DTO，承载组织基础信息、层级关系和状态的变更值。
 */
@Data
@Schema(description = "更新组织请求")
public class OrganizationUpdateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "名称")
    private String name;
    @Schema(description = "组织类型")
    private Integer orgType;
    @Schema(description = "父级编号")
    private Long parentId;
    @Schema(description = "层级路径")
    private String ancestors;
    @Schema(description = "层级深度")
    private Integer level;
    @Schema(description = "负责人姓名")
    private String contactName;
    @Schema(description = "联系电话")
    private String contactMobile;
    @Schema(description = "状态")
    private StatusEnum status;
}
