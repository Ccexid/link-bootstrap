package me.link.bootstrap.interfaces.dto.response.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 加密公钥响应。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "API 加密公钥响应")
public class ApiCryptoPublicKeyResponseVO {

    @Schema(description = "RSA 公钥，Base64 DER 格式")
    private String publicKey;
}
