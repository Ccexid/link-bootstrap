package me.link.bootstrap.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.link.bootstrap.shared.kernel.database.mybatis.TenantBaseDO;

/**
 * 用户角色关联持久化对象，字段与 system_user_role 表结构保持对应。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "system_user_role")
public class UserRolePO extends TenantBaseDO {

    @TableId
    private Long id;
    @TableField(value = "user_id")
    private Long userId;
    @TableField(value = "role_id")
    private Long roleId;
}
