package me.link.bootstrap.system.domain.tenant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.link.bootstrap.core.common.BaseEntity;

/**
 * 租户套餐聚合根
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_tenant_package", autoResultMap = true)
@Schema(description = "租户套餐信息")
public class TenantPackage extends BaseEntity {

    @Schema(description = "套餐名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String packageName;

    @Schema(description = "关联菜单权限 ID 集合", example = "1,2,3")
    private String menuIds;

    @Schema(description = "状态（0 正常 1 停用）")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    /**
     * DDD 行为：判断套餐是否可用
     */
    public boolean isEnable() {
        return Integer.valueOf(0).equals(this.status);
    }
}