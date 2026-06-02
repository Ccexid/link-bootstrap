package me.link.bootstrap.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.link.bootstrap.domain.valueobject.StatusEnum;
import me.link.bootstrap.shared.kernel.database.mybatis.BaseDO;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "system_tenant_package", autoResultMap = true)
public class TenantPackagePO extends BaseDO {

    @TableId
    private Long id;

    @TableField(value = "name")
    private String name;

    @TableField(value = "`status`")
    private StatusEnum status;

    @TableField(value = "remark")
    private String remark;

    @TableField(typeHandler = JacksonTypeHandler.class, value = "menu_ids")
    private Set<Long> menuIds;
}
