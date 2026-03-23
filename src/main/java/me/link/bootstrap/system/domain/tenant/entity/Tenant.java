package me.link.bootstrap.system.domain.tenant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.link.bootstrap.core.common.BaseEntity;
import java.time.LocalDateTime;

/**
 * 租户聚合根
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_tenant")
@Schema(description = "租户信息")
public class Tenant extends BaseEntity {

    @Schema(description = "上级租户 ID (0表示顶级)")
    private Long parentId;

    @Schema(description = "关联套餐 ID")
    private Long packageId;

    @Schema(description = "租户名称")
    private String tenantName;

    @Schema(description = "租户类型：P(平台), S(服务商), B(商家)")
    private String tenantType;

    @Schema(description = "租户链路溯源 (层级路径)")
    private String tenantPath;

    @Schema(description = "联系人")
    private String contactUser;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "租户状态 (0正常 1停用)")
    private Integer status;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    /**
     * DDD 行为：计算租户层级路径
     * @param parent 上级租户对象
     */
    public void computePath(Tenant parent) {
        if (parent == null || this.parentId == null || this.parentId == 0L) {
            this.parentId = 0L;
            this.tenantPath = "0";
        } else {
            this.tenantPath = parent.getTenantPath() + "," + parent.getId();
        }
    }
}