package me.link.bootstrap.system.interfaces.tenant.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.link.bootstrap.core.common.PageReq;

/**
 * 租户分页查询请求 VO
 * 放置在 interfaces 层或相关的 vo 包下
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "租户分页查询请求")
public class TenantPageReqVO extends PageReq {

    @Schema(description = "租户名称，支持模糊匹配", example = "领客源码")
    private String tenantName;

    @Schema(description = "联系人名称，支持模糊匹配", example = "张三")
    private String contactUser;

    @Schema(description = "租户类型：P(平台), S(服务商), B(商家)", example = "S")
    private String tenantType;

    @Schema(description = "状态 (0正常 1停用)", example = "0")
    private Integer status;

}