package me.link.bootstrap.interfaces.dto.response.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token 响应 VO，返回当前登录凭证及有效期信息。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Token 响应")
public class TokenResponseVO {

    @Schema(description = "Token 名称(请求头 key,默认 Authorization)", example = "Authorization")
    private String tokenName;

    @Schema(description = "Token 值(不含 Bearer 前缀,前端使用时拼为 Bearer {token})")
    private String tokenValue;

    @Schema(description = "Token 前缀", example = "Bearer")
    private String tokenPrefix;

    @Schema(description = "Token 剩余有效期,单位秒", example = "3600")
    private long tokenTimeout;

    @Schema(description = "Token 剩余无操作有效期,单位秒", example = "1800")
    private long tokenActiveTimeout;
}
