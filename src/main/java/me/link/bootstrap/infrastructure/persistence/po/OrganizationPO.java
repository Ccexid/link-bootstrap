package me.link.bootstrap.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.link.bootstrap.domain.valueobject.StatusEnum;
import me.link.bootstrap.shared.kernel.database.mybatis.TenantBaseDO;

/**
 * 组织持久化对象，字段与 system_organization 表结构保持对应。
 */
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
    @TableField(value = "contact_mobile_cipher", updateStrategy = FieldStrategy.ALWAYS)
    private String contactMobileCipher;
    @TableField(value = "contact_mobile_hash", updateStrategy = FieldStrategy.ALWAYS)
    private String contactMobileHash;
    @TableField(value = "contact_mobile_mask", updateStrategy = FieldStrategy.ALWAYS)
    private String contactMobileMask;
    @TableField(value = "contact_mobile_key_version")
    private Integer contactMobileKeyVersion;
    @TableField(value = "`status`")
    private StatusEnum status;
}
