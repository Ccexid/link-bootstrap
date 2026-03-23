package me.link.bootstrap.system.interfaces.tenant.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.link.bootstrap.core.common.PageReq;

/**
 * 租户套餐分页查询请求 VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "租户套餐分页查询请求")
public class TenantPackagePageReqVO extends PageReq {

    @Schema(description = "套餐名称（支持模糊匹配）", example = "旗舰版")
    private String packageName;

    @Schema(description = "状态（0 正常 1 停用）", example = "0")
    private Integer status;

}
