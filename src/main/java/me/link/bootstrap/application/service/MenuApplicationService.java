package me.link.bootstrap.application.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.support.ApplicationAssert;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.valueobject.StatusEnum;
import me.link.bootstrap.infrastructure.persistence.internal.MenuInternalService;
import me.link.bootstrap.infrastructure.persistence.po.MenuPO;
import me.link.bootstrap.infrastructure.persistence.support.PageOrderHelper;
import me.link.bootstrap.infrastructure.security.PermissionCacheService;
import me.link.bootstrap.interfaces.dto.request.menu.MenuCreateRequest;
import me.link.bootstrap.interfaces.dto.request.menu.MenuPageRequest;
import me.link.bootstrap.interfaces.dto.request.menu.MenuUpdateRequest;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 菜单服务，直接编排菜单持久化和权限缓存失效。
 * <p>菜单是全局权限资源，菜单权限或状态变化后需要主动失效权限缓存。</p>
 */
@Service
@RequiredArgsConstructor
public class MenuApplicationService {

    private static final Map<String, String> SORT_FIELD_MAPPING = Map.of(
            "id", "id",
            "created_at", "create_time",
            "updated_at", "update_time",
            "name", "name",
            "sort", "sort",
            "parent_id", "parent_id"
    );

    private final MenuInternalService menuInternalService;
    private final PermissionCacheService permissionCacheService;

    @Transactional
    public MenuPO create(MenuCreateRequest request) {
        MenuPO menu = new MenuPO();
        applyMutableFields(menu, request.getName(), request.getPermission(), request.getType(), request.getSort(), request.getParentId(), request.getPath(), request.getIcon(), request.getComponent(), request.getComponentName(), request.getStatus(), request.getVisible(), request.getKeepAlive(), request.getAlwaysShow());
        menuInternalService.save(menu);
        return menu;
    }

    public MenuPO get(Long id) {
        return ApplicationAssert.requireFound(menuInternalService.getById(id), ErrorCode.MENU_NOT_FOUND);
    }

    public PageResult<MenuPO> page(MenuPageRequest request) {
        Page<MenuPO> page = Page.of(request.getPageNo(), request.getPageSize());
        PageOrderHelper.applyOrders(page, request.getSortingFields(), SORT_FIELD_MAPPING);
        LambdaQueryWrapper<MenuPO> wrapper = new LambdaQueryWrapper<MenuPO>()
                .like(StrUtil.isNotBlank(request.getName()), MenuPO::getName, request.getName())
                .like(StrUtil.isNotBlank(request.getPermission()), MenuPO::getPermission, request.getPermission())
                .eq(request.getType() != null, MenuPO::getType, request.getType())
                .eq(request.getParentId() != null, MenuPO::getParentId, request.getParentId())
                .eq(request.getStatus() != null, MenuPO::getStatus, request.getStatus())
                .orderByDesc(request.getSortingFields() == null || request.getSortingFields().isEmpty(), MenuPO::getId);
        Page<MenuPO> result = menuInternalService.page(page, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal());
    }

    @Transactional
    public MenuPO update(Long id, MenuUpdateRequest request) {
        MenuPO menu = get(id);
        applyMutableFields(menu, request.getName(), request.getPermission(), request.getType(), request.getSort(), request.getParentId(), request.getPath(), request.getIcon(), request.getComponent(), request.getComponentName(), request.getStatus(), request.getVisible(), request.getKeepAlive(), request.getAlwaysShow());
        ApplicationAssert.requireSuccess(menuInternalService.updateById(menu), ErrorCode.MENU_NOT_FOUND);
        // 菜单是全局表,permission/status 变更影响所有用户,全量失效权限缓存
        permissionCacheService.evictAll();
        return get(id);
    }

    @Transactional
    public void delete(Long id) {
        ApplicationAssert.requireSuccess(menuInternalService.removeById(id), ErrorCode.MENU_NOT_FOUND);
        permissionCacheService.evictAll();
    }

    private static void applyMutableFields(MenuPO menu,
                                           String name,
                                           String permission,
                                           Integer type,
                                           Integer sort,
                                           Long parentId,
                                           String path,
                                           String icon,
                                           String component,
                                           String componentName,
                                           StatusEnum status,
                                           Boolean visible,
                                           Boolean keepAlive,
                                           Boolean alwaysShow) {
        if (StrUtil.isBlank(name)) {
            throw new IllegalArgumentException("菜单name不能为空");
        }
        menu.setName(name.trim());
        menu.setPermission(permission);
        menu.setType(type);
        menu.setSort(sort);
        menu.setParentId(parentId);
        menu.setPath(path);
        menu.setIcon(icon);
        menu.setComponent(component);
        menu.setComponentName(componentName);
        menu.setStatus(status);
        menu.setVisible(visible);
        menu.setKeepAlive(keepAlive);
        menu.setAlwaysShow(alwaysShow);
    }
}
