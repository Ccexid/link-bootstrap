package me.link.bootstrap.interfaces.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 手机验证码登录请求 DTO。
 */
@Data
@Schema(description = "手机验证码登录请求")
public class MobileLoginRequest {

    @NotBlank(message = "手机号码不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    @Schema(description = "完整手机号码", example = "13800000001")
    private String mobile;

    @NotBlank(message = "验证码不能为空")
    @Size(min = 4, max = 8, message = "验证码长度必须在 4 到 8 之间")
    @Schema(description = "短信验证码", example = "123456")
    private String code;
}
