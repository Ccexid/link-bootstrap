package me.link.bootstrap.application.service;

import me.link.bootstrap.infrastructure.persistence.internal.RoleInternalService;
import me.link.bootstrap.infrastructure.persistence.internal.UserInternalService;
import me.link.bootstrap.infrastructure.persistence.internal.UserRoleInternalService;
import me.link.bootstrap.infrastructure.persistence.po.RolePO;
import me.link.bootstrap.infrastructure.persistence.po.UserPO;
import me.link.bootstrap.infrastructure.persistence.po.UserRolePO;
import me.link.bootstrap.infrastructure.security.PermissionCacheService;
import me.link.bootstrap.interfaces.dto.request.userrole.UserRoleCreateRequest;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import me.link.bootstrap.shared.kernel.util.SecurityHelper;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserRoleApplicationServiceTest {

    private final UserRoleInternalService userRoleInternalService = mock(UserRoleInternalService.class);
    private final UserInternalService userInternalService = mock(UserInternalService.class);
    private final RoleInternalService roleInternalService = mock(RoleInternalService.class);
    private final PermissionCacheService permissionCacheService = mock(PermissionCacheService.class);
    private final UserRoleApplicationService userRoleApplicationService = new UserRoleApplicationService(
            userRoleInternalService,
            userInternalService,
            roleInternalService,
            permissionCacheService
    );

    @Test
    void shouldRejectUserOutsideCurrentTenantWhenCreatingBinding() {
        UserPO user = new UserPO();
        user.setId(1L);
        user.setTenantId(20L);
        when(userInternalService.getById(1L)).thenReturn(user);

        try (MockedStatic<SecurityHelper> securityHelper = mockStatic(SecurityHelper.class)) {
            securityHelper.when(SecurityHelper::getRequiredTenantId).thenReturn(10L);

            assertThatThrownBy(() -> userRoleApplicationService.create(request(1L, 2L)))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.USER_NOT_FOUND);
        }

        verify(userRoleInternalService, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldRejectRoleOutsideCurrentTenantWhenCreatingBinding() {
        UserPO user = new UserPO();
        user.setId(1L);
        user.setTenantId(10L);
        RolePO role = new RolePO();
        role.setId(2L);
        role.setTenantId(20L);
        when(userInternalService.getById(1L)).thenReturn(user);
        when(roleInternalService.getById(2L)).thenReturn(role);

        try (MockedStatic<SecurityHelper> securityHelper = mockStatic(SecurityHelper.class)) {
            securityHelper.when(SecurityHelper::getRequiredTenantId).thenReturn(10L);

            assertThatThrownBy(() -> userRoleApplicationService.create(request(1L, 2L)))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.ROLE_NOT_FOUND);
        }

        verify(userRoleInternalService, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldRejectDuplicateBindingWhenCreatingBinding() {
        UserPO user = new UserPO();
        user.setId(1L);
        user.setTenantId(10L);
        RolePO role = new RolePO();
        role.setId(2L);
        role.setTenantId(10L);
        when(userInternalService.getById(1L)).thenReturn(user);
        when(roleInternalService.getById(2L)).thenReturn(role);
        when(userRoleInternalService.exists(any(com.baomidou.mybatisplus.core.conditions.Wrapper.class)))
                .thenReturn(true);

        try (MockedStatic<SecurityHelper> securityHelper = mockStatic(SecurityHelper.class)) {
            securityHelper.when(SecurityHelper::getRequiredTenantId).thenReturn(10L);

            assertThatThrownBy(() -> userRoleApplicationService.create(request(1L, 2L)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("用户角色关联已存在");
        }

        verify(userRoleInternalService, never()).save(org.mockito.ArgumentMatchers.any(UserRolePO.class));
    }

    private UserRoleCreateRequest request(Long userId, Long roleId) {
        UserRoleCreateRequest request = new UserRoleCreateRequest();
        request.setUserId(userId);
        request.setRoleId(roleId);
        return request;
    }
}
