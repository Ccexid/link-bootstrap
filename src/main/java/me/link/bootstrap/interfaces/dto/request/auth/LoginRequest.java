package me.link.bootstrap.interfaces.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 登录请求 DTO，承载账号、密码和租户定位信息。
 */
@Data
@Schema(description = "登录请求")
public class LoginRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 30, message = "用户名长度必须在 2 到 30 之间")
    @Schema(description = "用户名", example = "admin")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 64, message = "密码长度必须在 8 到 64 之间")
    @Schema(description = "密码(明文,服务端加密比对)", example = "admin@123")
    private String password;

    @NotNull(message = "租户ID不能为空")
    @Schema(description = "租户编号", example = "1")
    private Long tenantId;
}
