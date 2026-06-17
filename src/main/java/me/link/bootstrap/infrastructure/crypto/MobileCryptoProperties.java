package me.link.bootstrap.infrastructure.crypto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 手机号落库保护配置，绑定 {@code link.mobile-crypto.*}。
 */
@Data
@ConfigurationProperties(prefix = "link.mobile-crypto")
public class MobileCryptoProperties {

    /** AES-GCM 加密密钥，建议生产环境使用 32 字节以上随机值并通过环境变量注入。 */
    private String encryptionKey = "link-bootstrap-dev-mobile-encryption-key";

    /** HMAC-SHA256 检索哈希密钥，必须与加密密钥分离。 */
    private String hashKey = "link-bootstrap-dev-mobile-hash-key";

    /** 当前密钥版本，后续轮换密钥时随新写入数据递增。 */
    private Integer keyVersion = 1;
}
