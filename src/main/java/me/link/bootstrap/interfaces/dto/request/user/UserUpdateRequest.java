package me.link.bootstrap.interfaces.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import me.link.bootstrap.domain.valueobject.StatusEnum;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 更新用户请求 DTO，承载用户基础资料、状态和组织归属的变更值。
 */
@Data
@Schema(description = "更新用户请求")
public class UserUpdateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户账号(同租户内唯一)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户账号不能为空")
    @Size(min = 2, max = 30, message = "用户账号长度必须在 2 到 30 之间")
    private String username;

    @Schema(description = "用户密码(明文,服务端加密后落库;每次更新都会被重新加密)",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户密码不能为空")
    @Size(min = 8, max = 64, message = "用户密码长度必须在 8 到 64 之间")
    private String password;

    @Schema(description = "用户昵称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户昵称不能为空")
    private String nickname;

    @Schema(description = "身份类型:1供应商 2平台 3商家 4用户", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "身份类型不能为空")
    private Integer userType;

    @Schema(description = "完整手机号码(服务端加密、哈希并脱敏后落库)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "手机号码不能为空")
    private String mobile;

    @Schema(description = "头像地址")
    private String avatar;

    @Schema(description = "账号状态(0正常 1停用)")
    private StatusEnum status;

    @Schema(description = "所属组织编号")
    private Long orgId;

    @Schema(description = "平台内部部门编号")
    private Long deptId;

    @Schema(description = "最后登录地址")
    private String loginIp;

    @Schema(description = "最后登录时间")
    private LocalDateTime loginDate;
}
