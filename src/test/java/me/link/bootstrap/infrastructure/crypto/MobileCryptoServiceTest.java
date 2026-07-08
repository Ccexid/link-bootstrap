package me.link.bootstrap.infrastructure.crypto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MobileCryptoServiceTest {

    /**
     * 验证 shouldProtectMobileWithCipherHashAndMask 场景。
     */
    @Test
    void shouldProtectMobileWithCipherHashAndMask() {
        MobileCryptoProperties properties = new MobileCryptoProperties();
        properties.setEncryptionKey("test-mobile-encryption-key");
        properties.setHashKey("test-mobile-hash-key");
        properties.setKeyVersion(7);
        MobileCryptoService service = new MobileCryptoService(properties);

        ProtectedMobile protectedMobile = service.protect(" 13800000001 ");

        assertThat(protectedMobile.cipher()).isNotBlank().doesNotContain("13800000001");
        assertThat(protectedMobile.hash()).hasSize(64);
        assertThat(protectedMobile.hash()).isEqualTo(service.hashForLookup("13800000001"));
        assertThat(protectedMobile.mask()).isEqualTo("138****0001");
        assertThat(protectedMobile.keyVersion()).isEqualTo(7);
        assertThat(service.decrypt(protectedMobile.cipher())).isEqualTo("13800000001");
    }

    /**
     * 验证 shouldReturnEmptyProtectionForBlankMobile 场景。
     */
    @Test
    void shouldReturnEmptyProtectionForBlankMobile() {
        MobileCryptoService service = new MobileCryptoService(new MobileCryptoProperties());

        ProtectedMobile protectedMobile = service.protect(" ");

        assertThat(protectedMobile.cipher()).isNull();
        assertThat(protectedMobile.hash()).isNull();
        assertThat(protectedMobile.mask()).isNull();
        assertThat(protectedMobile.keyVersion()).isEqualTo(1);
    }
}
