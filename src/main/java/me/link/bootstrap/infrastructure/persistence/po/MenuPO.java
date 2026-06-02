package me.link.bootstrap.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.link.bootstrap.domain.valueobject.StatusEnum;
import me.link.bootstrap.shared.kernel.database.mybatis.BaseDO;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "system_menu")
public class MenuPO extends BaseDO {

    @TableId
    private Long id;
    @TableField(value = "name")
    private String name;
    @TableField(value = "permission")
    private String permission;
    @TableField(value = "type")
    private Integer type;
    @TableField(value = "sort")
    private Integer sort;
    @TableField(value = "parent_id")
    private Long parentId;
    @TableField(value = "path")
    private String path;
    @TableField(value = "icon")
    private String icon;
    @TableField(value = "component")
    private String component;
    @TableField(value = "component_name")
    private String componentName;
    @TableField(value = "`status`")
    private StatusEnum status;
    @TableField(value = "visible")
    private Boolean visible;
    @TableField(value = "keep_alive")
    private Boolean keepAlive;
    @TableField(value = "always_show")
    private Boolean alwaysShow;
}
