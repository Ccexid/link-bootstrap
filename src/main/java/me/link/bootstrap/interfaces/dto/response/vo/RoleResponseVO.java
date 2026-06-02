package me.link.bootstrap.interfaces.dto.response.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import me.link.bootstrap.domain.valueobject.StatusEnum;
import me.link.bootstrap.shared.kernel.annotation.Sortable;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "角色信息")
public class RoleResponseVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    @Sortable(description = "id")
    private Long id;
    @Schema(description = "name")
    @Sortable(description = "name")
    private String name;
    @Schema(description = "code")
    @Sortable(description = "code")
    private String code;
    @Schema(description = "sort")
    @Sortable(description = "sort")
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
