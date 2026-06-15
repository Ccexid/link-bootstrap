package me.link.bootstrap.interfaces.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import me.link.bootstrap.domain.valueobject.StatusEnum;
import me.link.bootstrap.interfaces.dto.request.SortablePageRequest;

import java.io.Serial;

/**
 * 用户分页查询请求 DTO，承载用户筛选条件、分页参数和排序字段。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "用户分页查询请求")
public class UserPageRequest extends SortablePageRequest {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户账号(模糊匹配)")
    private String username;

    @Schema(description = "用户昵称(模糊匹配)")
    private String nickname;

    @Schema(description = "手机号码(模糊匹配)")
    private String mobile;

    @Schema(description = "身份类型:1供应商 2平台 3商家 4用户")
    private Integer userType;

    @Schema(description = "账号状态(0正常 1停用)")
    private StatusEnum status;
}
