package me.link.bootstrap.application.service;

import me.link.bootstrap.infrastructure.persistence.internal.MenuInternalService;
import me.link.bootstrap.infrastructure.security.PermissionCacheService;
import me.link.bootstrap.interfaces.dto.request.menu.MenuCreateRequest;
import me.link.bootstrap.shared.kernel.valueobject.StatusEnum;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MenuApplicationServiceTest {

    private final MenuInternalService menuInternalService = mock(MenuInternalService.class);
    private final PermissionCacheService permissionCacheService = mock(PermissionCacheService.class);
    private final MenuApplicationService menuApplicationService = new MenuApplicationService(
            menuInternalService,
            permissionCacheService
    );

    @Test
    void shouldEvictPermissionCacheWhenCreatingMenu() {
        when(menuInternalService.save(org.mockito.ArgumentMatchers.any())).thenReturn(true);

        menuApplicationService.create(createRequest());

        verify(permissionCacheService).evictAll();
    }

    private MenuCreateRequest createRequest() {
        MenuCreateRequest request = new MenuCreateRequest();
        request.setName("社区板块");
        request.setPermission("community:section:create");
        request.setType(2);
        request.setSort(10);
        request.setParentId(0L);
        request.setPath("/community/section");
        request.setComponent("community/section/index");
        request.setStatus(StatusEnum.NORMAL);
        request.setVisible(true);
        request.setKeepAlive(true);
        request.setAlwaysShow(false);
        return request;
    }
}
