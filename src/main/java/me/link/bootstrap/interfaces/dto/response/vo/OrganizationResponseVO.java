package me.link.bootstrap.interfaces.dto.response.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import me.link.bootstrap.domain.valueobject.StatusEnum;
import me.link.bootstrap.shared.kernel.annotation.Sortable;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "组织信息")
public class OrganizationResponseVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    @Sortable(description = "id")
    private Long id;
    @Schema(description = "name")
    @Sortable(description = "name")
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
    @Schema(description = "tenantId")
    @Sortable(description = "tenantId")
    private Long tenantId;
    @Schema(description = "createdAt")
    @Sortable(description = "createdAt")
    private LocalDateTime createdAt;
    @Schema(description = "updatedAt")
    @Sortable(description = "updatedAt")
    private LocalDateTime updatedAt;
}
