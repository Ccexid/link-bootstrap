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
@Schema(description = "令牌响应")
public class TokenResponseVO {

    @Schema(description = "令牌名称(请求头键,默认授权头)", example = "Authorization")
    private String tokenName;

    @Schema(description = "令牌值(不含令牌前缀,前端使用时需拼接前缀)")
    private String tokenValue;

    @Schema(description = "令牌前缀", example = "Bearer")
    private String tokenPrefix;

    @Schema(description = "令牌剩余有效期,单位秒", example = "3600")
    private long tokenTimeout;

    @Schema(description = "令牌剩余无操作有效期,单位秒", example = "1800")
    private long tokenActiveTimeout;
}
