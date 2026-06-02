package me.link.bootstrap.interfaces.dto.request.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import me.link.bootstrap.domain.valueobject.StatusEnum;

import java.time.LocalDateTime;
import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(description = "更新菜单请求")
public class MenuUpdateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "name")
    private String name;
    @Schema(description = "permission")
    private String permission;
    @Schema(description = "type")
    private Integer type;
    @Schema(description = "sort")
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
}
