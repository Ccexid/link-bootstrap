package me.link.bootstrap.interfaces.dto.request.organization;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import me.link.bootstrap.domain.valueobject.StatusEnum;

import java.time.LocalDateTime;
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

    @Schema(description = "name")
    private String name;
    @Schema(description = "orgType")
    private Integer orgType;
    @Schema(description = "parentId")
    private Long parentId;
    @Schema(description = "ancestors")
    private String ancestors;
    @Schema(description = "level")
    private Integer level;
    @Schema(description = "contactName")
    private String contactName;
    @Schema(description = "contactMobile")
    private String contactMobile;
    @Schema(description = "status")
    private StatusEnum status;
}
