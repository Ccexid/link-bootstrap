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

    @Schema(description = "令牌名称(请求头键,默认授权头)", example = "Authorization")
    private String tokenName;

    @Schema(description = "令牌值(不含令牌前缀,前端使用时需拼接前缀)")
    private String tokenValue;
}
