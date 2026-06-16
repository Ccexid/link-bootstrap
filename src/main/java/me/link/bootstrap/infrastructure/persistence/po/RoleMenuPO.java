package me.link.bootstrap.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.link.bootstrap.shared.kernel.database.mybatis.TenantBaseDO;

/**
 * 角色菜单关联持久化对象，字段与 system_role_menu 表结构保持对应。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "system_role_menu")
public class RoleMenuPO extends TenantBaseDO {

    @TableId
    private Long id;
    @TableField(value = "role_id")
    private Long roleId;
    @TableField(value = "menu_id")
    private Long menuId;
}
