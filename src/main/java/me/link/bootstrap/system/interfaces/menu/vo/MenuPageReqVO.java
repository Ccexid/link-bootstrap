package me.link.bootstrap.system.interfaces.menu.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.link.bootstrap.core.common.PageReq;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "菜单查询请求")
public class MenuPageReqVO extends PageReq {
    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "状态")
    private Integer status;
}