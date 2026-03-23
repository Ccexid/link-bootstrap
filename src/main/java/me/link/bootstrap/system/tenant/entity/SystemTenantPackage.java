package me.link.bootstrap.system.tenant.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.link.bootstrap.core.common.BaseEntity;

import java.util.Set;

/**
 * 租户套餐实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_tenant_package", autoResultMap = true)
@Schema(description = "租户套餐信息")
public class SystemTenantPackage extends BaseEntity {

    @Schema(description = "套餐名称")
    private String packageName;

    /**
     * 关联菜单权限 ID 集合
     * 使用 JacksonTypeHandler 自动将数据库字符串转为 Set
     */
    @Schema(description = "关联菜单权限 ID 集合")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Set<Long> menuIds;

    @Schema(description = "状态（0 正常 1 停用）")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}