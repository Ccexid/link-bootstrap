package me.link.bootstrap.infrastructure.crypto;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

/**
 * 手机号落库保护服务。
 * <p>
 * 对外仍接收完整手机号；落库时生成可逆密文、检索哈希和脱敏展示值。列表/详情接口默认返回脱敏值，
 * 需要发送短信等强业务场景再通过 {@link #decrypt(String)} 恢复完整手机号。
 * </p>
 */
public class MobileCryptoService {

    private static final String AES_ALGORITHM = "AES";
    private static final String AES_GCM_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final int GCM_IV_BYTES = 12;
    private static final int GCM_TAG_BITS = 128;

    private final SecureRandom secureRandom = new SecureRandom();
    private final SecretKeySpec encryptionKey;
    private final SecretKeySpec hashKey;
    private final Integer keyVersion;

    public MobileCryptoService(MobileCryptoProperties properties) {
        Assert.notNull(properties, "mobile crypto properties must not be null");
        Assert.hasText(properties.getEncryptionKey(), "link.mobile-crypto.encryption-key must not be blank");
        Assert.hasText(properties.getHashKey(), "link.mobile-crypto.hash-key must not be blank");
        Assert.isTrue(!properties.getEncryptionKey().equals(properties.getHashKey()), "mobile encryption key and hash key must be different");
        Assert.notNull(properties.getKeyVersion(), "link.mobile-crypto.key-version must not be null");
        this.encryptionKey = new SecretKeySpec(sha256(properties.getEncryptionKey()), AES_ALGORITHM);
        this.hashKey = new SecretKeySpec(properties.getHashKey().getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
        this.keyVersion = properties.getKeyVersion();
    }

    public ProtectedMobile protect(String mobile) {
        String normalized = normalize(mobile);
        if (!StringUtils.hasText(normalized)) {
            return new ProtectedMobile(null, null, null, keyVersion);
        }
        return new ProtectedMobile(encrypt(normalized), hash(normalized), mask(normalized), keyVersion);
    }

    public String decrypt(String cipherText) {
        if (!StringUtils.hasText(cipherText)) {
            return null;
        }
        try {
            byte[] payload = Base64.getDecoder().decode(cipherText);
            ByteBuffer buffer = ByteBuffer.wrap(payload);
            byte[] iv = new byte[GCM_IV_BYTES];
            buffer.get(iv);
            byte[] cipherBytes = new byte[buffer.remaining()];
            buffer.get(cipherBytes);

            Cipher cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, encryptionKey, new GCMParameterSpec(GCM_TAG_BITS, iv));
            return new String(cipher.doFinal(cipherBytes), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException | IllegalArgumentException ex) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, ex);
        }
    }

    public String hashForLookup(String mobile) {
        String normalized = normalize(mobile);
        return StringUtils.hasText(normalized) ? hash(normalized) : null;
    }

    public String mask(String mobile) {
        String normalized = normalize(mobile);
        if (!StringUtils.hasText(normalized)) {
            return null;
        }
        if (normalized.length() <= 7) {
            return normalized.charAt(0) + "****" + normalized.charAt(normalized.length() - 1);
        }
        return normalized.substring(0, 3) + "****" + normalized.substring(normalized.length() - 4);
    }

    private String encrypt(String plainText) {
        try {
            byte[] iv = new byte[GCM_IV_BYTES];
            secureRandom.nextBytes(iv);
            Cipher cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey, new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] cipherBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            ByteBuffer buffer = ByteBuffer.allocate(iv.length + cipherBytes.length);
            buffer.put(iv);
            buffer.put(cipherBytes);
            return Base64.getEncoder().encodeToString(buffer.array());
        } catch (GeneralSecurityException ex) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, ex);
        }
    }

    private String hash(String mobile) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(hashKey);
            return HexFormat.of().formatHex(mac.doFinal(mobile.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException ex) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, ex);
        }
    }

    private static String normalize(String mobile) {
        return StringUtils.hasText(mobile) ? mobile.trim() : null;
    }

    private static byte[] sha256(String text) {
        try {
            return MessageDigest.getInstance("SHA-256")
                    .digest(text.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException ex) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, ex);
        }
    }
}
