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
@TableName(value = "system_organization")
public class OrganizationPO extends TenantBaseDO {

    @TableId
    private Long id;
    @TableField(value = "name")
    private String name;
    @TableField(value = "org_type")
    private Integer orgType;
    @TableField(value = "parent_id")
    private Long parentId;
    @TableField(value = "ancestors")
    private String ancestors;
    @TableField(value = "level")
    private Integer level;
    @TableField(value = "contact_name")
    private String contactName;
    @TableField(value = "contact_mobile")
    private String contactMobile;
    @TableField(value = "`status`")
    private StatusEnum status;
}
