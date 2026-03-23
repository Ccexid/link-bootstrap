package me.link.bootstrap.system.domain.menu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.link.bootstrap.core.common.BaseEntity;

/**
 * 菜单权限聚合根
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
@Schema(description = "菜单权限信息")
public class Menu extends BaseEntity {

    @Schema(description = "菜单名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String menuName;

    @Schema(description = "父菜单 ID (0表示顶级)")
    private Long parentId;

    @Schema(description = "显示顺序")
    private Integer orderNum;

    @Schema(description = "路由地址")
    private String path;

    @Schema(description = "组件路径")
    private String component;

    @Schema(description = "权限标识")
    private String perms;

    @Schema(description = "菜单类型 (M目录 C菜单 F按钮)")
    private String menuType;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "状态 (0显示 1隐藏)")
    private Integer status;

    @Schema(description = "租户类型 (P/S/B，用于过滤不同身份可见菜单)")
    private String tenantType;
}