package me.link.bootstrap.infrastructure.crypto;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import me.link.bootstrap.application.support.ApplicationAssert;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * API RSA 加解密服务。
 */
public class ApiCryptoService {

    private final ApiCryptoProperties properties;
    private final RSA decryptRsa;
    private final RSA encryptRsa;

    /**
     * 创建API加密实例。
     */
    public ApiCryptoService(ApiCryptoProperties properties) {
        Assert.notNull(properties, "api crypto properties must not be null");
        Assert.hasText(properties.getPrivateKey(), "link.api-crypto.private-key must not be blank");
        Assert.hasText(properties.getPublicKey(), "link.api-crypto.public-key must not be blank");
        this.properties = properties;
        this.decryptRsa = new RSA(properties.getPrivateKey(), null);
        this.encryptRsa = new RSA(null, properties.getPublicKey());
    }

    /**
     * 解密请求。
     */
    public String decryptRequest(String encryptedText) {
        if (!StringUtils.hasText(encryptedText)) {
            ApplicationAssert.invalidParam("请求密文不能为空");
        }
        return decryptRsa.decryptStr(encryptedText, KeyType.PrivateKey, StandardCharsets.UTF_8);
    }

    /**
     * 加密响应。
     */
    public String encryptResponse(String plainText) {
        if (plainText == null) {
            return null;
        }
        return encryptRsa.encryptBase64(plainText, StandardCharsets.UTF_8, KeyType.PublicKey);
    }

    /**
     * 返回。
     */
    public ApiCryptoProperties properties() {
        return properties;
    }
}
