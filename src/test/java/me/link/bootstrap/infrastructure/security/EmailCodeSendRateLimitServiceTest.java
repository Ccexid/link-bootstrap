package me.link.bootstrap.infrastructure.security;

import me.link.bootstrap.infrastructure.config.LinkSecurityProperties;
import me.link.bootstrap.shared.kernel.config.ClientIpProperties;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmailCodeSendRateLimitServiceTest {

    private final RedissonClient redissonClient = mock(RedissonClient.class);
    private final LinkSecurityProperties securityProperties = new LinkSecurityProperties();
    private final ClientIpProperties clientIpProperties = new ClientIpProperties();
    private final RAtomicLong counter = mock(RAtomicLong.class);
    private final EmailCodeSendRateLimitService rateLimitService =
            new EmailCodeSendRateLimitService(redissonClient, securityProperties, clientIpProperties);

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void shouldLimitByClientIpAndEmailWithoutPlainEmailInRedisKey() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("10.0.0.1");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        when(redissonClient.getAtomicLong(startsWith("link:email-code:send:"))).thenReturn(counter);
        when(counter.incrementAndGet()).thenReturn(1L);

        rateLimitService.check("Admin@Example.com");

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(redissonClient).getAtomicLong(keyCaptor.capture());
        assertThat(keyCaptor.getValue())
                .startsWith("link:email-code:send:")
                .doesNotContain("Admin@Example.com")
                .doesNotContain("admin@example.com");
        verify(counter).expire(securityProperties.getEmailCode().getSendIpEmailWindow());
    }

    @Test
    void shouldRejectWhenIpEmailCombinationExceedsLimit() {
        when(redissonClient.getAtomicLong(startsWith("link:email-code:send:"))).thenReturn(counter);
        when(counter.incrementAndGet()).thenReturn(
                securityProperties.getEmailCode().getSendIpEmailMaxRequests() + 1
        );

        assertThatThrownBy(() -> rateLimitService.check("admin@example.com"))
                .isInstanceOfSatisfying(BusinessException.class, ex ->
                        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.RATE_LIMITED));
    }
}
