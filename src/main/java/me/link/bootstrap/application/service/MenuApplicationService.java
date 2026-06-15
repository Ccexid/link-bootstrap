package me.link.bootstrap.application.service;

import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.support.ApplicationAssert;
import me.link.bootstrap.application.command.CreateMenuCommand;
import me.link.bootstrap.application.command.MenuPageQuery;
import me.link.bootstrap.application.command.UpdateMenuCommand;
import me.link.bootstrap.domain.entity.MenuEntity;
import me.link.bootstrap.domain.factory.MenuFactory;
import me.link.bootstrap.domain.repository.MenuRepository;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.infrastructure.security.PermissionCacheService;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 菜单应用服务，负责编排菜单创建、查询、更新和删除流程。
 * <p>菜单是全局权限资源，菜单权限或状态变化后需要主动失效权限缓存。</p>
 */
@Service
@RequiredArgsConstructor
public class MenuApplicationService {

    private final MenuRepository menuRepository;
    private final PermissionCacheService permissionCacheService;

    @Transactional
    public MenuEntity create(CreateMenuCommand command) {
        MenuEntity menu = MenuFactory.create(command.name(), command.permission(), command.type(), command.sort(), command.parentId(), command.path(), command.icon(), command.component(), command.componentName(), command.status(), command.visible(), command.keepAlive(), command.alwaysShow());
        return menuRepository.save(menu);
    }

    public MenuEntity get(Long id) {
        return ApplicationAssert.requireFound(menuRepository.findById(id), ErrorCode.MENU_NOT_FOUND);
    }

    public PageResult<MenuEntity> page(MenuPageQuery query) {
        return menuRepository.page(query.pageNo(), query.pageSize(), query.name(), query.permission(), query.type(), query.parentId(), query.status(), query.sortingFields());
    }

    @Transactional
    public MenuEntity update(UpdateMenuCommand command) {
        MenuEntity menu = get(command.id());
        MenuFactory.changeBasicInfo(menu, command.name(), command.permission(), command.type(), command.sort(), command.parentId(), command.path(), command.icon(), command.component(), command.componentName(), command.status(), command.visible(), command.keepAlive(), command.alwaysShow());
        ApplicationAssert.requireSuccess(menuRepository.update(menu), ErrorCode.MENU_NOT_FOUND);
        // 菜单是全局表,permission/status 变更影响所有用户,全量失效权限缓存
        permissionCacheService.evictAll();
        return get(command.id());
    }

    @Transactional
    public void delete(Long id) {
        ApplicationAssert.requireSuccess(menuRepository.deleteById(id), ErrorCode.MENU_NOT_FOUND);
        permissionCacheService.evictAll();
    }
}
