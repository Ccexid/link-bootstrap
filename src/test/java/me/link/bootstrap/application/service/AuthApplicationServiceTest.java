package me.link.bootstrap.application.service;

import me.link.bootstrap.infrastructure.persistence.mapper.PermissionMapper;
import me.link.bootstrap.infrastructure.persistence.po.UserPO;
import me.link.bootstrap.infrastructure.security.EmailCodeSendRateLimitService;
import me.link.bootstrap.infrastructure.security.EmailCodeService;
import me.link.bootstrap.infrastructure.security.HumanVerificationService;
import me.link.bootstrap.infrastructure.security.LoginAttemptService;
import me.link.bootstrap.interfaces.dto.request.auth.SendEmailCodeRequest;
import me.link.bootstrap.shared.kernel.valueobject.StatusEnum;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthApplicationServiceTest {

    private final UserApplicationService userApplicationService = mock(UserApplicationService.class);
    private final PermissionMapper permissionMapper = mock(PermissionMapper.class);
    private final LoginAttemptService loginAttemptService = mock(LoginAttemptService.class);
    private final EmailCodeService emailCodeService = mock(EmailCodeService.class);
    private final HumanVerificationService humanVerificationService = mock(HumanVerificationService.class);
    private final EmailCodeSendRateLimitService emailCodeSendRateLimitService = mock(EmailCodeSendRateLimitService.class);

    private final AuthApplicationService authApplicationService = new AuthApplicationService(
            userApplicationService,
            permissionMapper,
            loginAttemptService,
            emailCodeService,
            humanVerificationService,
            emailCodeSendRateLimitService
    );

    @Test
    void shouldNotRevealWhetherEmailUserExistsWhenSendingEmailCode() {
        SendEmailCodeRequest request = request("missing@example.com", "captcha-token");
        when(userApplicationService.findByEmailForLogin("missing@example.com")).thenReturn(List.of());

        assertThatNoException().isThrownBy(() -> authApplicationService.sendEmailCode(request));

        verify(humanVerificationService).verify("captcha-token");
        verify(emailCodeSendRateLimitService).check("missing@example.com");
        verify(emailCodeService, never()).send(anyString());
    }

    @Test
    void shouldNotSendEmailCodeToDisabledUserButKeepUnifiedResponse() {
        SendEmailCodeRequest request = request("disabled@example.com", "captcha-token");
        UserPO user = user(1L, 10L, StatusEnum.DISABLE);
        when(userApplicationService.findByEmailForLogin("disabled@example.com")).thenReturn(List.of(user));

        assertThatNoException().isThrownBy(() -> authApplicationService.sendEmailCode(request));

        verify(emailCodeService, never()).send(anyString());
    }

    @Test
    void shouldSendEmailCodeToEnabledUser() {
        SendEmailCodeRequest request = request("admin@example.com", "captcha-token");
        UserPO user = user(1L, 10L, StatusEnum.NORMAL);
        when(userApplicationService.findByEmailForLogin("admin@example.com")).thenReturn(List.of(user));

        authApplicationService.sendEmailCode(request);

        verify(emailCodeService).send("admin@example.com");
    }

    private SendEmailCodeRequest request(String email, String captchaToken) {
        SendEmailCodeRequest request = new SendEmailCodeRequest();
        request.setEmail(email);
        request.setCaptchaToken(captchaToken);
        return request;
    }

    private UserPO user(Long id, Long tenantId, StatusEnum status) {
        UserPO user = new UserPO();
        user.setId(id);
        user.setTenantId(tenantId);
        user.setStatus(status);
        return user;
    }
}
