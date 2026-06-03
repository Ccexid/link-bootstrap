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

@Data
@Schema(description = "创建用户请求")
public class UserCreateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户账号(同租户内唯一)", requiredMode = Schema.RequiredMode.REQUIRED, example = "alice")
    @NotBlank(message = "用户账号不能为空")
    @Size(min = 2, max = 30, message = "用户账号长度必须在 2 到 30 之间")
    private String username;

    @Schema(description = "用户密码(明文,服务端 BCrypt 加密后落库)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户密码不能为空")
    @Size(min = 8, max = 64, message = "用户密码长度必须在 8 到 64 之间")
    private String password;

    @Schema(description = "用户昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "爱丽丝")
    @NotBlank(message = "用户昵称不能为空")
    private String nickname;

    @Schema(description = "身份类型:1供应商S 2平台P 3商家B 4用户C", requiredMode = Schema.RequiredMode.REQUIRED, example = "3")
    @NotNull(message = "身份类型不能为空")
    private Integer userType;

    @Schema(description = "手机号码", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800000001")
    @NotBlank(message = "手机号码不能为空")
    private String mobile;

    @Schema(description = "头像 URL")
    private String avatar;

    @Schema(description = "账号状态(0正常 1停用)", example = "0")
    private StatusEnum status;

    @Schema(description = "所属组织 ID")
    private Long orgId;

    @Schema(description = "平台内部部门 ID(仅 P 端使用)")
    private Long deptId;

    @Schema(description = "最后登录 IP(后端自动填写,前端可不传)")
    private String loginIp;

    @Schema(description = "最后登录时间(后端自动填写,前端可不传)")
    private LocalDateTime loginDate;
}
