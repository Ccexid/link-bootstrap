package me.link.bootstrap.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.link.bootstrap.domain.valueobject.StatusEnum;
import me.link.bootstrap.shared.kernel.database.mybatis.TenantBaseDO;

/**
 * 角色持久化对象，字段与 system_role 表结构保持对应。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "system_role")
public class RolePO extends TenantBaseDO {

    @TableId
    private Long id;
    @TableField(value = "name")
    private String name;
    @TableField(value = "code")
    private String code;
    @TableField(value = "sort")
    private Integer sort;
    @TableField(value = "data_scope")
    private Integer dataScope;
    @TableField(value = "data_scope_dept_ids")
    private String dataScopeDeptIds;
    @TableField(value = "`status`")
    private StatusEnum status;
    @TableField(value = "type")
    private Integer type;
    @TableField(value = "remark")
    private String remark;
}
