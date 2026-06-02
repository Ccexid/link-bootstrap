package me.link.bootstrap.interfaces.dto.response.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import me.link.bootstrap.domain.valueobject.StatusEnum;
import me.link.bootstrap.shared.kernel.annotation.Sortable;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "菜单信息")
public class MenuResponseVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    @Sortable(description = "id")
    private Long id;
    @Schema(description = "name")
    @Sortable(description = "name")
    private String name;
    @Schema(description = "permission")
    private String permission;
    @Schema(description = "type")
    private Integer type;
    @Schema(description = "sort")
    @Sortable(description = "sort")
    private Integer sort;
    @Schema(description = "parentId")
    private Long parentId;
    @Schema(description = "path")
    private String path;
    @Schema(description = "icon")
    private String icon;
    @Schema(description = "component")
    private String component;
    @Schema(description = "componentName")
    private String componentName;
    @Schema(description = "status")
    private StatusEnum status;
    @Schema(description = "visible")
    private Boolean visible;
    @Schema(description = "keepAlive")
    private Boolean keepAlive;
    @Schema(description = "alwaysShow")
    private Boolean alwaysShow;
    @Schema(description = "createdAt")
    @Sortable(description = "createdAt")
    private LocalDateTime createdAt;
    @Schema(description = "updatedAt")
    @Sortable(description = "updatedAt")
    private LocalDateTime updatedAt;
}
