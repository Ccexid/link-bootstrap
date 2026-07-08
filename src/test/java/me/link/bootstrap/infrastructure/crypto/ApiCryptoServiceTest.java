package me.link.bootstrap.infrastructure.crypto;

import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class ApiCryptoServiceTest {

    /**
     * 验证 shouldEncryptAndDecryptText 场景。
     */
    @Test
    void shouldEncryptAndDecryptText() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();

        ApiCryptoProperties properties = new ApiCryptoProperties();
        properties.setPrivateKey(Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
        properties.setPublicKey(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));

        ApiCryptoService apiCryptoService = new ApiCryptoService(properties);
        String plainText = "{\"username\":\"admin\",\"password\":\"123456\"}";

        String encryptedText = apiCryptoService.encryptResponse(plainText);

        assertThat(apiCryptoService.decryptRequest(encryptedText)).isEqualTo(plainText);
    }
}
