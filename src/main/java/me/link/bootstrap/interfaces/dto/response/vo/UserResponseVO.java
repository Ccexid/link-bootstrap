package me.link.bootstrap.interfaces.dto.response.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import me.link.bootstrap.domain.valueobject.StatusEnum;
import me.link.bootstrap.shared.kernel.annotation.Sortable;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户响应 VO，定义接口返回给前端的用户字段。
 */
@Data
@Schema(description = "用户信息")
public class UserResponseVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "编号")
    @Sortable(description = "id")
    private Long id;
    @Schema(description = "用户账号")
    @Sortable(description = "username")
    private String username;
    @Schema(description = "用户昵称")
    private String nickname;
    @Schema(description = "用户类型")
    private Integer userType;
    @Schema(description = "手机号码")
    private String mobile;
    @Schema(description = "头像地址")
    private String avatar;
    @Schema(description = "状态")
    private StatusEnum status;
    @Schema(description = "所属组织编号")
    private Long orgId;
    @Schema(description = "平台内部部门编号")
    private Long deptId;
    @Schema(description = "最后登录地址")
    private String loginIp;
    @Schema(description = "最后登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loginDate;
    @Schema(description = "租户编号")
    @Sortable(description = "tenantId")
    private Long tenantId;
    @Schema(description = "创建时间")
    @Sortable(description = "createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @Schema(description = "更新时间")
    @Sortable(description = "updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
