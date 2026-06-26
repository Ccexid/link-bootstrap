package me.link.bootstrap.interfaces.dto.request.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import me.link.bootstrap.shared.kernel.valueobject.StatusEnum;

import java.io.Serial;
import java.io.Serializable;

/**
 * 更新菜单请求 DTO，承载可变更的菜单基础信息和前端路由配置。
 */
@Data
@Schema(description = "更新菜单请求")
public class MenuUpdateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "名称")
    private String name;
    @Schema(description = "权限标识")
    private String permission;
    @Schema(description = "类型")
    private Integer type;
    @Schema(description = "显示顺序")
    private Integer sort;
    @Schema(description = "父级编号")
    private Long parentId;
    @Schema(description = "路由路径")
    private String path;
    @Schema(description = "图标")
    private String icon;
    @Schema(description = "组件路径")
    private String component;
    @Schema(description = "组件名称")
    private String componentName;
    @Schema(description = "状态")
    private StatusEnum status;
    @Schema(description = "是否可见")
    private Boolean visible;
    @Schema(description = "是否缓存")
    private Boolean keepAlive;
    @Schema(description = "是否总是显示")
    private Boolean alwaysShow;
}
