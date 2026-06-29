package me.link.bootstrap.application.service;

import cn.hutool.crypto.digest.BCrypt;
import me.link.bootstrap.infrastructure.crypto.MobileCryptoService;
import me.link.bootstrap.infrastructure.crypto.ProtectedMobile;
import me.link.bootstrap.infrastructure.persistence.internal.UserInternalService;
import me.link.bootstrap.infrastructure.persistence.po.UserPO;
import me.link.bootstrap.interfaces.dto.request.user.UserUpdateRequest;
import me.link.bootstrap.shared.kernel.util.SecurityHelper;
import me.link.bootstrap.shared.kernel.valueobject.StatusEnum;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class UserApplicationServiceTest {

    private final UserInternalService userInternalService = mock(UserInternalService.class);
    private final MobileCryptoService mobileCryptoService = mock(MobileCryptoService.class);
    private final UserApplicationService userApplicationService = new UserApplicationService(
            userInternalService,
            mobileCryptoService
    );

    @Test
    void shouldPreserveExistingPasswordWhenUpdatePasswordIsBlank() {
        UserPO user = existingUser();
        when(userInternalService.getById(1L)).thenReturn(user);
        when(userInternalService.updateById(user)).thenReturn(true);
        when(mobileCryptoService.protect("13800000001")).thenReturn(protectedMobile());

        try (MockedStatic<SecurityHelper> securityHelper = mockStatic(SecurityHelper.class)) {
            securityHelper.when(SecurityHelper::getRequiredTenantId).thenReturn(10L);

            userApplicationService.update(1L, updateRequest(" "));
        }

        assertThat(user.getPassword()).isEqualTo("old-password-hash");
    }

    @Test
    void shouldHashNewPasswordWhenUpdatePasswordIsPresent() {
        UserPO user = existingUser();
        when(userInternalService.getById(1L)).thenReturn(user);
        when(userInternalService.updateById(user)).thenReturn(true);
        when(mobileCryptoService.protect("13800000001")).thenReturn(protectedMobile());

        try (MockedStatic<SecurityHelper> securityHelper = mockStatic(SecurityHelper.class)) {
            securityHelper.when(SecurityHelper::getRequiredTenantId).thenReturn(10L);

            userApplicationService.update(1L, updateRequest("newPass123"));
        }

        assertThat(user.getPassword()).isNotEqualTo("old-password-hash");
        assertThat(BCrypt.checkpw("newPass123", user.getPassword())).isTrue();
    }

    private UserPO existingUser() {
        UserPO user = new UserPO();
        user.setId(1L);
        user.setPassword("old-password-hash");
        user.setTenantId(10L);
        return user;
    }

    private UserUpdateRequest updateRequest(String password) {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setUsername("alice");
        request.setPassword(password);
        request.setNickname("Alice");
        request.setUserType(3);
        request.setMobile("13800000001");
        request.setEmail("alice@example.com");
        request.setStatus(StatusEnum.NORMAL);
        return request;
    }

    private ProtectedMobile protectedMobile() {
        return new ProtectedMobile("mobile-cipher", "mobile-hash", "138****0001", 1);
    }
}
