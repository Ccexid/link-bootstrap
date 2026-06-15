package me.link.bootstrap.interfaces.dto.response.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应 VO，返回客户端登录后需要保存的 Token 信息。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录响应")
public class LoginResponseVO {

    @Schema(description = "Token 名称(请求头 key,默认 Authorization)", example = "Authorization")
    private String tokenName;

    @Schema(description = "Token 值(不含 Bearer 前缀,前端使用时拼为 Bearer {token})")
    private String tokenValue;
}
