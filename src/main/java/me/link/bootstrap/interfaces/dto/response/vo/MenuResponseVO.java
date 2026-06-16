package me.link.bootstrap.interfaces.dto.response.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import me.link.bootstrap.domain.valueobject.StatusEnum;
import me.link.bootstrap.shared.kernel.annotation.Sortable;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 菜单响应 VO，定义接口返回给前端的菜单字段。
 */
@Data
@Schema(description = "菜单信息")
public class MenuResponseVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "编号")
    @Sortable(description = "id")
    private Long id;
    @Schema(description = "名称")
    @Sortable(description = "name")
    private String name;
    @Schema(description = "权限标识")
    private String permission;
    @Schema(description = "类型")
    private Integer type;
    @Schema(description = "显示顺序")
    @Sortable(description = "sort")
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
    @Schema(description = "创建时间")
    @Sortable(description = "createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @Schema(description = "更新时间")
    @Sortable(description = "updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
