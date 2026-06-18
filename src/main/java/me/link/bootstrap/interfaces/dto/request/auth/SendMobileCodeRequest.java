package me.link.bootstrap.interfaces.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 发送手机验证码请求 DTO。
 */
@Data
@Schema(description = "发送手机验证码请求")
public class SendMobileCodeRequest {

    @NotBlank(message = "手机号码不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    @Schema(description = "完整手机号码", example = "13800000001")
    private String mobile;
}
