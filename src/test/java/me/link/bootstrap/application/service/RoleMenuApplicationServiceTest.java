package me.link.bootstrap.application.service;

import me.link.bootstrap.infrastructure.persistence.internal.MenuInternalService;
import me.link.bootstrap.infrastructure.persistence.internal.RoleInternalService;
import me.link.bootstrap.infrastructure.persistence.internal.RoleMenuInternalService;
import me.link.bootstrap.infrastructure.persistence.po.MenuPO;
import me.link.bootstrap.infrastructure.persistence.po.RolePO;
import me.link.bootstrap.infrastructure.persistence.po.RoleMenuPO;
import me.link.bootstrap.infrastructure.security.PermissionCacheService;
import me.link.bootstrap.interfaces.dto.request.rolemenu.RoleMenuCreateRequest;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import me.link.bootstrap.shared.kernel.util.SecurityHelper;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RoleMenuApplicationServiceTest {

    private final RoleMenuInternalService roleMenuInternalService = mock(RoleMenuInternalService.class);
    private final RoleInternalService roleInternalService = mock(RoleInternalService.class);
    private final MenuInternalService menuInternalService = mock(MenuInternalService.class);
    private final PermissionCacheService permissionCacheService = mock(PermissionCacheService.class);
    private final RoleMenuApplicationService roleMenuApplicationService = new RoleMenuApplicationService(
            roleMenuInternalService,
            roleInternalService,
            menuInternalService,
            permissionCacheService
    );

    @Test
    void shouldRejectRoleOutsideCurrentTenantWhenCreatingBinding() {
        RolePO role = new RolePO();
        role.setId(1L);
        role.setTenantId(20L);
        when(roleInternalService.getById(1L)).thenReturn(role);

        try (MockedStatic<SecurityHelper> securityHelper = mockStatic(SecurityHelper.class)) {
            securityHelper.when(SecurityHelper::getRequiredTenantId).thenReturn(10L);

            assertThatThrownBy(() -> roleMenuApplicationService.create(request(1L, 2L)))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.ROLE_NOT_FOUND);
        }

        verify(roleMenuInternalService, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldRejectMissingMenuWhenCreatingBinding() {
        RolePO role = new RolePO();
        role.setId(1L);
        role.setTenantId(10L);
        when(roleInternalService.getById(1L)).thenReturn(role);
        when(menuInternalService.getById(2L)).thenReturn(null);

        try (MockedStatic<SecurityHelper> securityHelper = mockStatic(SecurityHelper.class)) {
            securityHelper.when(SecurityHelper::getRequiredTenantId).thenReturn(10L);

            assertThatThrownBy(() -> roleMenuApplicationService.create(request(1L, 2L)))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.MENU_NOT_FOUND);
        }

        verify(roleMenuInternalService, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldRejectDuplicateBindingWhenCreatingBinding() {
        RolePO role = new RolePO();
        role.setId(1L);
        role.setTenantId(10L);
        MenuPO menu = new MenuPO();
        menu.setId(2L);
        when(roleInternalService.getById(1L)).thenReturn(role);
        when(menuInternalService.getById(2L)).thenReturn(menu);
        when(roleMenuInternalService.exists(any(com.baomidou.mybatisplus.core.conditions.Wrapper.class)))
                .thenReturn(true);

        try (MockedStatic<SecurityHelper> securityHelper = mockStatic(SecurityHelper.class)) {
            securityHelper.when(SecurityHelper::getRequiredTenantId).thenReturn(10L);

            assertThatThrownBy(() -> roleMenuApplicationService.create(request(1L, 2L)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("角色菜单关联已存在");
        }

        verify(roleMenuInternalService, never()).save(org.mockito.ArgumentMatchers.any(RoleMenuPO.class));
    }

    private RoleMenuCreateRequest request(Long roleId, Long menuId) {
        RoleMenuCreateRequest request = new RoleMenuCreateRequest();
        request.setRoleId(roleId);
        request.setMenuId(menuId);
        return request;
    }
}
