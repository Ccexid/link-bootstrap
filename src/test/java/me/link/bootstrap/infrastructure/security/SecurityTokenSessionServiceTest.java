package me.link.bootstrap.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import me.link.bootstrap.infrastructure.config.LinkSecurityProperties;
import me.link.bootstrap.infrastructure.persistence.po.UserPO;
import me.link.bootstrap.shared.kernel.constant.SecurityConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

class SecurityTokenSessionServiceTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-07-07T08:00:00Z"), ZoneOffset.UTC);

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RBucket<SecurityTokenSession> tokenBucket;

    @Mock
    private PermissionCacheService permissionCacheService;

    private SecurityTokenSessionService securityTokenSessionService;

    /**
     * 准备测试上下文。
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        LinkSecurityProperties properties = new LinkSecurityProperties();
        properties.getToken().setTimeout(Duration.ofSeconds(60));
        properties.getToken().setActiveTimeout(Duration.ofSeconds(30));
        properties.getToken().setKeyPrefix("link:security:token:");
        when(redissonClient.<SecurityTokenSession>getBucket(anyString())).thenReturn(tokenBucket);
        securityTokenSessionService = new SecurityTokenSessionService(
                redissonClient,
                permissionCacheService,
                properties,
                FIXED_CLOCK
        );
    }

    /**
     * 验证 createShouldStoreOpaqueTokenSessionInRedis 场景。
     */
    @Test
    void createShouldStoreOpaqueTokenSessionInRedis() {
        UserPO user = new UserPO();
        user.setId(1001L);
        user.setTenantId(2001L);
        user.setUserType(2);
        when(permissionCacheService.getPermissions(1001L)).thenReturn(List.of("system:user:list"));
        when(permissionCacheService.getRoleCodes(1001L)).thenReturn(List.of(SecurityConstants.ROLE_SUPER_ADMIN));

        SecurityTokenSession session = securityTokenSessionService.create(user);

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<SecurityTokenSession> sessionCaptor = ArgumentCaptor.forClass(SecurityTokenSession.class);
        verify(redissonClient).getBucket(keyCaptor.capture());
        verify(tokenBucket).set(sessionCaptor.capture(), eq(Duration.ofSeconds(60)));

        assertThat(session.getToken()).isNotBlank();
        assertThat(keyCaptor.getValue()).isEqualTo("link:security:token:" + session.getToken());
        assertThat(sessionCaptor.getValue().getUserId()).isEqualTo(1001L);
        assertThat(sessionCaptor.getValue().getTenantId()).isEqualTo(2001L);
        assertThat(sessionCaptor.getValue().isSuperAdmin()).isTrue();
        assertThat(sessionCaptor.getValue().getPermissions()).containsExactly("system:user:list");
    }

    /**
     * 验证 loadShouldRejectIdleExpiredSessionAndDeleteRedisKey 场景。
     */
    @Test
    void loadShouldRejectIdleExpiredSessionAndDeleteRedisKey() {
        SecurityTokenSession idleExpired = new SecurityTokenSession();
        idleExpired.setToken("token-value");
        idleExpired.setUserId(1001L);
        idleExpired.setTenantId(2001L);
        idleExpired.setUserType(2);
        idleExpired.setLastActiveAtEpochSecond(FIXED_CLOCK.instant().minusSeconds(31).getEpochSecond());
        idleExpired.setExpireAtEpochSecond(FIXED_CLOCK.instant().plusSeconds(29).getEpochSecond());
        when(tokenBucket.get()).thenReturn(idleExpired);

        Optional<SecurityTokenSession> loaded = securityTokenSessionService.load("token-value");

        assertThat(loaded).isEmpty();
        verify(tokenBucket).delete();
    }

    /**
     * 验证 refreshShouldExtendAbsoluteAndActiveTimeouts 场景。
     */
    @Test
    void refreshShouldExtendAbsoluteAndActiveTimeouts() {
        SecurityTokenSession session = new SecurityTokenSession();
        session.setToken("token-value");
        session.setUserId(1001L);
        session.setTenantId(2001L);
        session.setUserType(2);
        session.setLastActiveAtEpochSecond(FIXED_CLOCK.instant().minusSeconds(10).getEpochSecond());
        session.setExpireAtEpochSecond(FIXED_CLOCK.instant().plusSeconds(20).getEpochSecond());
        when(tokenBucket.get()).thenReturn(session);

        SecurityTokenSession refreshed = securityTokenSessionService.refresh("token-value");

        assertThat(refreshed.getExpireAtEpochSecond()).isEqualTo(FIXED_CLOCK.instant().plusSeconds(60).getEpochSecond());
        assertThat(refreshed.getLastActiveAtEpochSecond()).isEqualTo(FIXED_CLOCK.instant().getEpochSecond());
        verify(tokenBucket).set(refreshed, Duration.ofSeconds(60));
    }
}
