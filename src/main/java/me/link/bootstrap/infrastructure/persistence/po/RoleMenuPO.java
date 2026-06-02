package me.link.bootstrap.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.link.bootstrap.domain.valueobject.StatusEnum;
import me.link.bootstrap.shared.kernel.database.mybatis.TenantBaseDO;

import java.time.LocalDateTime;

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
