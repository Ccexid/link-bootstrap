package me.link.bootstrap.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.link.bootstrap.domain.valueobject.StatusEnum;
import me.link.bootstrap.shared.kernel.database.mybatis.TenantBaseDO;

import java.time.LocalDateTime;

/**
 * 用户持久化对象，字段与 system_users 表结构保持对应。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "system_users")
public class UserPO extends TenantBaseDO {

    @TableId
    private Long id;
    @TableField(value = "username")
    private String username;
    @TableField(value = "password")
    private String password;
    @TableField(value = "nickname")
    private String nickname;
    @TableField(value = "user_type")
    private Integer userType;
    @TableField(value = "mobile_cipher")
    private String mobileCipher;
    @TableField(value = "mobile_hash")
    private String mobileHash;
    @TableField(value = "mobile_mask")
    private String mobileMask;
    @TableField(value = "mobile_key_version")
    private Integer mobileKeyVersion;
    @TableField(value = "avatar")
    private String avatar;
    @TableField(value = "`status`")
    private StatusEnum status;
    @TableField(value = "org_id")
    private Long orgId;
    @TableField(value = "dept_id")
    private Long deptId;
    @TableField(value = "login_ip")
    private String loginIp;
    @TableField(value = "login_date")
    private LocalDateTime loginDate;
}
