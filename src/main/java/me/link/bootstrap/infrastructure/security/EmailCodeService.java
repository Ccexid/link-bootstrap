package me.link.bootstrap.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.link.bootstrap.infrastructure.config.LinkSecurityProperties;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Year;
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
    private final MailProperties mailProperties;

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

    /**
     * 记录Verify失败。
     */
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

    /**
     * 发送Mail。
     */
    private void sendMail(String email, String code, LinkSecurityProperties.EmailCode properties) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "邮件服务未配置");
        }

        String from = StringUtils.defaultIfBlank(
                StringUtils.trimToNull(properties.getFrom()),
                StringUtils.trimToNull(mailProperties.getUsername()));
        if (StringUtils.isBlank(from)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "邮箱发件人未配置");
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            setFrom(helper, from, properties.getSenderName());
            helper.setTo(email);
            helper.setSubject(properties.getSubject());
            helper.setText(buildMailText(code, properties), buildMailHtml(code, properties));
            mailSender.send(message);
        } catch (MailException | MessagingException | UnsupportedEncodingException | IllegalStateException ex) {
            log.warn("邮箱验证码发送失败: emailKey={}", emailKey(email), ex);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "验证码发送失败");
        }
    }

    /**
     * 构建MailText。
     */
    private String buildMailText(String code, LinkSecurityProperties.EmailCode properties) {
        long ttlMinutes = Math.max(1L, properties.getTtl().toMinutes());
        return """
                您的登录验证码是：%s

                验证码 %d 分钟内有效，请勿泄露给他人。
                如果不是您本人操作，请忽略本邮件。
                """.formatted(code, ttlMinutes);
    }

    /**
     * 构建MailHTML。
     */
    private String buildMailHtml(String code, LinkSecurityProperties.EmailCode properties) {
        long ttlMinutes = Math.max(1L, properties.getTtl().toMinutes());
        String rawSystemName = StringUtils.defaultIfBlank(properties.getSystemName(), "Link Platform");
        String rawCompanyName = StringUtils.defaultIfBlank(properties.getCompanyName(), rawSystemName);
        String systemName = escapeHtml(rawSystemName);
        String companyName = escapeHtml(rawCompanyName);
        String escapedCode = escapeHtml(code);
        int year = Year.now().getValue();
        return """
                <!DOCTYPE html>
                <html lang="zh-CN">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>验证码邮件</title>
                </head>
                <body style="margin: 0; padding: 0; background-color: #f4f4f4; font-family: 'Helvetica Neue', Helvetica, Arial, 'Microsoft YaHei', sans-serif;">
                    <table border="0" cellpadding="0" cellspacing="0" width="100%%" style="background-color: #f4f4f4;">
                        <tr>
                            <td align="center" style="padding: 20px 0;">
                                <table border="0" cellpadding="0" cellspacing="0" width="600" style="background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); overflow: hidden; max-width: 600px;">
                                    <tr>
                                        <td style="background-color: #1890ff; padding: 20px 40px;" align="left">
                                            <span style="font-size: 20px; font-weight: bold; color: #ffffff; text-decoration: none;">%s</span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 40px 40px 20px 40px; color: #333333; font-size: 14px; line-height: 1.6;">
                                            <p style="margin: 0 0 15px 0;">尊敬的用户，您好！</p>
                                            <p style="margin: 0 0 15px 0;">您正在进行身份验证操作，请使用以下验证码完成验证：</p>
                                            <div style="text-align: center; margin: 30px 0;">
                                                <span style="font-size: 32px; font-weight: bold; color: #1890ff; letter-spacing: 6px; background-color: #f0f8ff; padding: 15px 30px; border-radius: 4px; border: 1px dashed #1890ff; display: inline-block;">%s</span>
                                            </div>
                                            <p style="margin: 0 0 10px 0; color: #999999; font-size: 12px;">
                                                * 验证码有效期为 <strong style="color: #ff4d4f;">%d</strong> 分钟，请尽快输入。
                                            </p>
                                            <p style="margin: 0 0 10px 0; color: #999999; font-size: 12px;">
                                                * 如果这不是您本人的操作，请忽略此邮件，您的账户安全不受影响。
                                            </p>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 20px 40px 40px 40px; border-top: 1px solid #eeeeee; color: #999999; font-size: 12px; line-height: 1.5;">
                                            <p style="margin: 0;">此邮件由系统自动发送，请勿直接回复。</p>
                                            <p style="margin: 5px 0 0 0;">&copy; %d %s. 保留所有权利。</p>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(systemName, escapedCode, ttlMinutes, year, companyName);
    }

    /**
     * 设置From。
     */
    private void setFrom(MimeMessageHelper helper, String from, String senderName)
            throws MessagingException, UnsupportedEncodingException {
        String normalizedSenderName = StringUtils.trimToNull(senderName);
        if (normalizedSenderName == null) {
            helper.setFrom(from);
            return;
        }
        helper.setFrom(from, normalizedSenderName);
    }

    /**
     * 转义HTML。
     */
    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * 生成编码。
     */
    private String generateCode(int length) {
        int bound = (int) Math.pow(10, length);
        return String.format("%0" + length + "d", SECURE_RANDOM.nextInt(bound));
    }

    /**
     * 规范化邮箱。
     */
    private String normalizeEmail(String email) {
        return StringUtils.trimToEmpty(email);
    }

    /**
     * 构建邮箱键。
     */
    private String emailKey(String email) {
        return sha256Hex(email);
    }

    /**
     * 构建验证码键。
     */
    private String codeKey(String emailKey) {
        return CODE_KEY_PREFIX + emailKey;
    }

    /**
     * 构建尝试键。
     */
    private String attemptKey(String emailKey) {
        return ATTEMPT_KEY_PREFIX + emailKey;
    }

    /**
     * 签名。
     */
    private String sign(String email, String code, String secret) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            return HexFormat.of().formatHex(mac.doFinal((email + ":" + code).getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, ex);
        }
    }

    /**
     * 计算256Hex。
     */
    private String sha256Hex(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, ex);
        }
    }

    /**
     * 校验Properties。
     */
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
