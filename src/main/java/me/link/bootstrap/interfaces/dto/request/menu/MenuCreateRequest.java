package me.link.bootstrap.interfaces.dto.request.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import me.link.bootstrap.domain.valueobject.StatusEnum;

import java.io.Serial;
import java.io.Serializable;

/**
 * 创建菜单请求 DTO，承载菜单基础信息、权限标识和前端路由配置。
 */
@Data
@Schema(description = "创建菜单请求")
public class MenuCreateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "菜单名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "用户管理")
    @NotBlank(message = "菜单名称不能为空")
    private String name;

    @Schema(description = "权限标识(对应权限校验注解,按系统模块动作三段式编码)",
            example = "system:user:create")
    private String permission;

    @Schema(description = "菜单类型:1目录 2菜单 3按钮", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "菜单类型不能为空")
    private Integer type;

    @Schema(description = "显示顺序", example = "10")
    private Integer sort;

    @Schema(description = "父菜单编号(0 代表顶级)", example = "0")
    private Long parentId;

    @Schema(description = "前端路由路径", example = "/user")
    private String path;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "前端组件路径", example = "system/user/index")
    private String component;

    @Schema(description = "前端组件名")
    private String componentName;

    @Schema(description = "菜单状态(0正常 1停用)")
    private StatusEnum status;

    @Schema(description = "是否可见(0显示 1隐藏)")
    private Boolean visible;

    @Schema(description = "是否缓存(0开启 1关闭)")
    private Boolean keepAlive;

    @Schema(description = "是否总是显示(0是 1否)")
    private Boolean alwaysShow;
}
