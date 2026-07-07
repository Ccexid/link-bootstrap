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

    public void revoke(String token) {
        if (token == null || token.isBlank()) {
            return;
        }
        bucket(token).delete();
    }

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

    private boolean isExpired(SecurityTokenSession session, long now) {
        return session.getExpireAtEpochSecond() <= now
                || session.getLastActiveAtEpochSecond() + activeTimeoutSeconds() <= now;
    }

    private List<String> safeList(List<String> values) {
        return values == null ? List.of() : List.copyOf(values);
    }

    private Duration remainingAbsoluteTtl(SecurityTokenSession session, long now) {
        return Duration.ofSeconds(Math.max(1, session.getExpireAtEpochSecond() - now));
    }

    private RBucket<SecurityTokenSession> bucket(String token) {
        return redissonClient.getBucket(properties.getToken().getKeyPrefix() + token);
    }

    private String generateToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private long nowEpochSecond() {
        return Instant.now(clock).getEpochSecond();
    }

    private Duration timeout() {
        return properties.getToken().getTimeout();
    }

    private long timeoutSeconds() {
        return timeout().toSeconds();
    }

    private long activeTimeoutSeconds() {
        return properties.getToken().getActiveTimeout().toSeconds();
    }
}
