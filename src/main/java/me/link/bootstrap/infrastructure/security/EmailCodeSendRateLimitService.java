package me.link.bootstrap.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.link.bootstrap.infrastructure.config.LinkSecurityProperties;
import me.link.bootstrap.shared.kernel.config.ClientIpProperties;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;

/**
 * 邮箱验证码发送限流。
 * <p>按客户端 IP + 邮箱组合限制发送频率，防止公开接口被用于邮箱枚举和邮件轰炸。</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailCodeSendRateLimitService {

    private static final String KEY_PREFIX = "link:email-code:send:";

    private final RedissonClient redissonClient;
    private final LinkSecurityProperties securityProperties;
    private final ClientIpProperties clientIpProperties;

    /**
     * 检查。
     */
    public void check(String email) {
        LinkSecurityProperties.EmailCode properties = securityProperties.getEmailCode();
        Duration window = properties.getSendIpEmailWindow();
        if (window == null || window.isNegative() || window.isZero() || properties.getSendIpEmailMaxRequests() < 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "邮箱验证码发送限流配置无效");
        }

        String clientIp = clientIp();
        String key = KEY_PREFIX + sha256(clientIp + ":" + StringUtils.trimToEmpty(email).toLowerCase());
        RAtomicLong counter = redissonClient.getAtomicLong(key);
        long current = counter.incrementAndGet();
        if (current == 1L) {
            counter.expire(window);
        }
        if (current > properties.getSendIpEmailMaxRequests()) {
            log.warn("邮箱验证码发送触发限流: key={}, current={}, max={}, windowSeconds={}",
                    key, current, properties.getSendIpEmailMaxRequests(), window.toSeconds());
            throw new BusinessException(ErrorCode.RATE_LIMITED, "验证码发送过于频繁,请稍后再试");
        }
    }

    /**
     * 解析IP。
     */
    private String clientIp() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes)) {
            return "";
        }
        HttpServletRequest request = attributes.getRequest();
        String ip = null;
        if (clientIpProperties.isTrustForwardHeaders()) {
            for (String headerName : clientIpProperties.getForwardHeaders()) {
                String headerValue = request.getHeader(headerName);
                if (StringUtils.isNotBlank(headerValue)) {
                    ip = pickRightmostIp(headerValue);
                    break;
                }
            }
        }
        return StringUtils.defaultIfBlank(ip, request.getRemoteAddr());
    }

    /**
     * 选择最右侧IP。
     */
    private String pickRightmostIp(String headerValue) {
        int lastComma = headerValue.lastIndexOf(',');
        String candidate = lastComma >= 0 ? headerValue.substring(lastComma + 1) : headerValue;
        return candidate.trim();
    }

    /**
     * 计算256。
     */
    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, ex);
        }
    }
}
