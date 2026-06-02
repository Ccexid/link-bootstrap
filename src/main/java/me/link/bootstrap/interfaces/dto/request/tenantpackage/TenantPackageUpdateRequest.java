package me.link.bootstrap.interfaces.dto.request.tenantpackage;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * 更新租户套餐请求 DTO，承载 HTTP 入参并声明基础参数校验规则。
 */
@Data
@Schema(description = "更新租户套餐请求")
public class TenantPackageUpdateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "套餐名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "套餐名不能为空")
    private String name;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "关联的菜单编号数组", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "关联菜单不能为空")
    private Set<Long> menuIds;

    @Schema(description = "是否启用")
    private Boolean enabled;
}
