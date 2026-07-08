package me.link.bootstrap.infrastructure.security;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.infrastructure.config.LinkSecurityProperties;
import me.link.bootstrap.infrastructure.persistence.po.UserPO;
import me.link.bootstrap.interfaces.dto.response.vo.TokenResponseVO;
import me.link.bootstrap.shared.kernel.constant.SecurityConstants;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityTokenSessionService {

    private static final int TOKEN_BYTES = 32;

    private final RedissonClient redissonClient;
    private final PermissionCacheService permissionCacheService;
    private final LinkSecurityProperties properties;
    private final Clock clock;
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * 创建安全令牌会话。
     */
    public SecurityTokenSession create(UserPO user) {
        long now = nowEpochSecond();
        List<String> roles = permissionCacheService.getRoleCodes(user.getId());
        List<String> permissions = permissionCacheService.getPermissions(user.getId());
        SecurityTokenSession session = new SecurityTokenSession(
                generateToken(),
                user.getId(),
                user.getTenantId(),
                user.getUserType(),
                roles.contains(SecurityConstants.ROLE_SUPER_ADMIN),
                safeList(permissions),
                safeList(roles),
                now,
                now,
                now + timeoutSeconds()
        );
        bucket(session.getToken()).set(session, timeout());
        return session;
    }

    /**
     * 加载安全令牌会话。
     */
    public Optional<SecurityTokenSession> load(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        RBucket<SecurityTokenSession> bucket = bucket(token);
        SecurityTokenSession session = bucket.get();
        if (session == null) {
            return Optional.empty();
        }
        long now = nowEpochSecond();
        if (isExpired(session, now)) {
            bucket.delete();
            return Optional.empty();
        }
        if (!properties.getToken().isAutoRenew()) {
            return Optional.of(session);
        }
        session.setLastActiveAtEpochSecond(now);
        bucket.set(session, remainingAbsoluteTtl(session, now));
        return Optional.of(session);
    }

    /**
     * 刷新。
     */
    public SecurityTokenSession refresh(String token) {
        RBucket<SecurityTokenSession> bucket = bucket(token);
        SecurityTokenSession session = bucket.get();
        if (session == null || isExpired(session, nowEpochSecond())) {
            bucket.delete();
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        long now = nowEpochSecond();
        session.setLastActiveAtEpochSecond(now);
        session.setExpireAtEpochSecond(now + timeoutSeconds());
        bucket.set(session, timeout());
        return session;
    }

    /**
     * 撤销。
     */
    public void revoke(String token) {
        if (token == null || token.isBlank()) {
            return;
        }
        bucket(token).delete();
    }

    /**
     * 转换为令牌响应。
     */
    public TokenResponseVO toTokenResponse(SecurityTokenSession session) {
        long now = nowEpochSecond();
        return new TokenResponseVO(
                properties.getToken().getTokenName(),
                session.getToken(),
                properties.getToken().getTokenPrefix(),
                Math.max(0, session.getExpireAtEpochSecond() - now),
                Math.max(0, session.getLastActiveAtEpochSecond() + activeTimeoutSeconds() - now)
        );
    }

    /**
     * 转换为主体。
     */
    public LoginUserPrincipal toPrincipal(SecurityTokenSession session) {
        return new LoginUserPrincipal(
                session.getUserId(),
                session.getTenantId(),
                session.getUserType(),
                session.isSuperAdmin(),
                session.getPermissions(),
                session.getRoles()
        );
    }

    /**
     * 判断过期是否成立。
     */
    private boolean isExpired(SecurityTokenSession session, long now) {
        return session.getExpireAtEpochSecond() <= now
                || session.getLastActiveAtEpochSecond() + activeTimeoutSeconds() <= now;
    }

    /**
     * 安全转换List。
     */
    private List<String> safeList(List<String> values) {
        return values == null ? List.of() : List.copyOf(values);
    }

    /**
     * 计算剩余绝对TTL。
     */
    private Duration remainingAbsoluteTtl(SecurityTokenSession session, long now) {
        return Duration.ofSeconds(Math.max(1, session.getExpireAtEpochSecond() - now));
    }

    /**
     * 获取。
     */
    private RBucket<SecurityTokenSession> bucket(String token) {
        return redissonClient.getBucket(properties.getToken().getKeyPrefix() + token);
    }

    /**
     * 生成令牌。
     */
    private String generateToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * 获取当前EpochSecond。
     */
    private long nowEpochSecond() {
        return Instant.now(clock).getEpochSecond();
    }

    /**
     * 计算。
     */
    private Duration timeout() {
        return properties.getToken().getTimeout();
    }

    /**
     * 计算Seconds。
     */
    private long timeoutSeconds() {
        return timeout().toSeconds();
    }

    /**
     * 计算活跃TimeoutSeconds。
     */
    private long activeTimeoutSeconds() {
        return properties.getToken().getActiveTimeout().toSeconds();
    }
}
