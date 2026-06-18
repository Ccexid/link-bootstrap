package me.link.bootstrap.interfaces.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 邮箱验证码登录请求 DTO。
 */
@Data
@Schema(description = "邮箱验证码登录请求")
public class EmailLoginRequest {

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;

    @NotBlank(message = "验证码不能为空")
    @Size(min = 4, max = 8, message = "验证码长度必须在 4 到 8 之间")
    @Schema(description = "邮箱验证码", example = "123456")
    private String code;
}
