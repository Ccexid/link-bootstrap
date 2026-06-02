package me.link.bootstrap.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.link.bootstrap.domain.valueobject.StatusEnum;
import me.link.bootstrap.shared.kernel.database.mybatis.BaseDO;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 租户持久化对象，字段与 system_tenant 表结构保持对应。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "system_tenant", autoResultMap = true)
public class TenantPO extends BaseDO {

    /**
     * 租户编号 (主键 ID)
     */
    @TableId
    private Long id;

    /**
     * 租户名
     */
    @TableField(value = "name")
    private String name;

    /**
     * 联系人的用户编号
     */
    @TableField(value = "contact_user_id")
    private Long contactUserId;

    /**
     * 联系人姓名
     */
    @TableField(value = "contact_name")
    private String contactName;

    /**
     * 联系手机
     */
    @TableField(value = "contact_mobile")
    private String contactMobile;

    /**
     * 租户状态 (0正常 1停用)
     */
    @TableField(value = "`status`")
    private StatusEnum status;

    /**
     * 绑定域名数组
     */
    @TableField(typeHandler = JacksonTypeHandler.class, value = "websites")
    private Set<String> websites;

    /**
     * 租户套餐编号
     */
    @TableField(value = "package_id")
    private Long packageId;

    /**
     * 过期时间
     */
    @TableField(value = "expire_time")
    private LocalDateTime expireTime;

    /**
     * 账号数量
     */
    @TableField(value = "account_count")
    private Integer accountCount;
}
