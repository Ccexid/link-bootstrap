package me.link.bootstrap.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.link.bootstrap.infrastructure.config.LinkSecurityProperties;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.HexFormat;

/**
 * 邮箱验证码服务。
 * <p>
 * 验证码只通过邮件明文发送,Redis 中仅保存带服务端密钥的 HMAC 摘要,并在校验成功后立即删除。
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailCodeService {

    private static final String CODE_KEY_PREFIX = "link:auth:email-code:";
    private static final String ATTEMPT_KEY_PREFIX = "link:auth:email-code-attempt:";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final RedissonClient redissonClient;
    private final LinkSecurityProperties securityProperties;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    /**
     * 生成验证码并发送到指定邮箱。
     */
    public void send(String email) {
        String normalizedEmail = normalizeEmail(email);
        LinkSecurityProperties.EmailCode properties = securityProperties.getEmailCode();
        validateProperties(properties);

        String emailKey = emailKey(normalizedEmail);
        String code = generateCode(properties.getLength());
        String codeKey = codeKey(emailKey);
        String attemptKey = attemptKey(emailKey);

        RBucket<String> codeBucket = redissonClient.getBucket(codeKey);
        codeBucket.set(sign(normalizedEmail, code, properties.getSecret()), properties.getTtl());
        redissonClient.getAtomicLong(attemptKey).delete();

        try {
            sendMail(normalizedEmail, code, properties);
        } catch (RuntimeException ex) {
            codeBucket.delete();
            throw ex;
        }

        log.info("邮箱验证码已发送: emailKey={}, ttlSeconds={}", emailKey, properties.getTtl().toSeconds());
    }

    /**
     * 校验验证码。校验成功后验证码立即失效。
     */
    public void verify(String email, String code) {
        String normalizedEmail = normalizeEmail(email);
        String normalizedCode = StringUtils.trimToEmpty(code);
        LinkSecurityProperties.EmailCode properties = securityProperties.getEmailCode();
        validateProperties(properties);

        String emailKey = emailKey(normalizedEmail);
        RBucket<String> codeBucket = redissonClient.getBucket(codeKey(emailKey));
        String expected = codeBucket.get();
        if (StringUtils.isBlank(expected)) {
            throw new BusinessException(ErrorCode.EMAIL_VERIFY_CODE_ERROR);
        }

        String actual = sign(normalizedEmail, normalizedCode, properties.getSecret());
        if (!MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), actual.getBytes(StandardCharsets.UTF_8))) {
            long failures = recordVerifyFailure(emailKey, properties.getMaxVerifyAttempts(), properties.getTtl());
            log.warn("邮箱验证码校验失败: emailKey={}, failures={}", emailKey, failures);
            throw new BusinessException(ErrorCode.EMAIL_VERIFY_CODE_ERROR);
        }

        codeBucket.delete();
        redissonClient.getAtomicLong(attemptKey(emailKey)).delete();
    }

    private long recordVerifyFailure(String emailKey, int maxVerifyAttempts, Duration ttl) {
        RAtomicLong counter = redissonClient.getAtomicLong(attemptKey(emailKey));
        long failures = counter.incrementAndGet();
        if (failures == 1L) {
            counter.expire(ttl);
        }
        if (failures >= maxVerifyAttempts) {
            redissonClient.getBucket(codeKey(emailKey)).delete();
        }
        return failures;
    }

    private void sendMail(String email, String code, LinkSecurityProperties.EmailCode properties) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "邮件服务未配置");
        }

        String from = StringUtils.defaultIfBlank(StringUtils.trimToNull(properties.getFrom()), StringUtils.trimToNull(mailUsername));
        if (StringUtils.isBlank(from)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "邮箱发件人未配置");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject(properties.getSubject());
        message.setText(buildMailText(code, properties));

        try {
            mailSender.send(message);
        } catch (MailException ex) {
            log.warn("邮箱验证码发送失败: emailKey={}", emailKey(email), ex);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "验证码发送失败");
        }
    }

    private String buildMailText(String code, LinkSecurityProperties.EmailCode properties) {
        long ttlMinutes = Math.max(1L, properties.getTtl().toMinutes());
        return """
                您的登录验证码是：%s

                验证码 %d 分钟内有效，请勿泄露给他人。
                如果不是您本人操作，请忽略本邮件。
                """.formatted(code, ttlMinutes);
    }

    private String generateCode(int length) {
        int bound = (int) Math.pow(10, length);
        return String.format("%0" + length + "d", SECURE_RANDOM.nextInt(bound));
    }

    private String normalizeEmail(String email) {
        return StringUtils.trimToEmpty(email);
    }

    private String emailKey(String email) {
        return sha256Hex(email);
    }

    private String codeKey(String emailKey) {
        return CODE_KEY_PREFIX + emailKey;
    }

    private String attemptKey(String emailKey) {
        return ATTEMPT_KEY_PREFIX + emailKey;
    }

    private String sign(String email, String code, String secret) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            return HexFormat.of().formatHex(mac.doFinal((email + ":" + code).getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("邮箱验证码签名失败", ex);
        }
    }

    private String sha256Hex(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 不可用", ex);
        }
    }

    private void validateProperties(LinkSecurityProperties.EmailCode properties) {
        if (properties.getLength() < 4 || properties.getLength() > 8) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "邮箱验证码长度必须在 4 到 8 位之间");
        }
        if (properties.getMaxVerifyAttempts() < 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "邮箱验证码最大校验次数必须大于 0");
        }
        if (properties.getTtl() == null || properties.getTtl().isNegative() || properties.getTtl().isZero()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "邮箱验证码有效期必须大于 0");
        }
        if (StringUtils.isBlank(properties.getSecret())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "邮箱验证码密钥未配置");
        }
    }
}
