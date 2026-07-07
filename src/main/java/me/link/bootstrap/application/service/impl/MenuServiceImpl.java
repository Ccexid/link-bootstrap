package me.link.bootstrap.application.service.impl;

import me.link.bootstrap.application.service.MenuService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.infrastructure.mapper.MenuMapper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.support.ApplicationAssert;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.valueobject.StatusEnum;
import me.link.bootstrap.infrastructure.persistence.po.MenuPO;
import me.link.bootstrap.interfaces.dto.response.vo.MenuResponseVO;
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
public class MenuServiceImpl extends ServiceImpl<MenuMapper, MenuPO> implements MenuService {

    private static final Map<String, String> SORT_FIELD_MAPPING = Map.of(
            "id", "id",
            "created_at", "create_time",
            "updated_at", "update_time",
            "name", "name",
            "sort", "sort",
            "parent_id", "parent_id"
    );
    private final PermissionCacheService permissionCacheService;

    @Transactional
    public MenuResponseVO create(MenuCreateRequest request) {
        MenuPO menu = new MenuPO();
        applyMutableFields(menu, request.getName(), request.getPermission(), request.getType(), request.getSort(), request.getParentId(), request.getPath(), request.getIcon(), request.getComponent(), request.getComponentName(), request.getStatus(), request.getVisible(), request.getKeepAlive(), request.getAlwaysShow());
        save(menu);
        permissionCacheService.evictAll();
        return toResponse(menu);
    }

    public MenuResponseVO get(Long id) {
        return toResponse(getRequired(id));
    }

    public PageResult<MenuResponseVO> page(MenuPageRequest request) {
        Page<MenuPO> page = Page.of(request.getPageNo(), request.getPageSize());
        PageOrderHelper.applyOrders(page, request.getSortingFields(), SORT_FIELD_MAPPING);
        LambdaQueryWrapper<MenuPO> wrapper = new LambdaQueryWrapper<MenuPO>()
                .like(StrUtil.isNotBlank(request.getName()), MenuPO::getName, request.getName())
                .like(StrUtil.isNotBlank(request.getPermission()), MenuPO::getPermission, request.getPermission())
                .eq(request.getType() != null, MenuPO::getType, request.getType())
                .eq(request.getParentId() != null, MenuPO::getParentId, request.getParentId())
                .eq(request.getStatus() != null, MenuPO::getStatus, request.getStatus())
                .orderByDesc(request.getSortingFields() == null || request.getSortingFields().isEmpty(), MenuPO::getId);
        Page<MenuPO> result = page(page, wrapper);
        return new PageResult<>(result.getRecords().stream().map(this::toResponse).toList(), result.getTotal());
    }

    @Transactional
    public MenuResponseVO update(Long id, MenuUpdateRequest request) {
        MenuPO menu = getRequired(id);
        applyMutableFields(menu, request.getName(), request.getPermission(), request.getType(), request.getSort(), request.getParentId(), request.getPath(), request.getIcon(), request.getComponent(), request.getComponentName(), request.getStatus(), request.getVisible(), request.getKeepAlive(), request.getAlwaysShow());
        ApplicationAssert.requireSuccess(updateById(menu), ErrorCode.MENU_NOT_FOUND);
        // 菜单是全局表,permission/status 变更影响所有用户,全量失效权限缓存
        permissionCacheService.evictAll();
        return get(id);
    }

    @Transactional
    public void delete(Long id) {
        ApplicationAssert.requireSuccess(removeById(id), ErrorCode.MENU_NOT_FOUND);
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
            ApplicationAssert.invalidParam("菜单name不能为空");
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

    private MenuPO getRequired(Long id) {
        return ApplicationAssert.requireFound(getById(id), ErrorCode.MENU_NOT_FOUND);
    }

    private MenuResponseVO toResponse(MenuPO source) {
        MenuResponseVO response = BeanUtil.copyProperties(source, MenuResponseVO.class);
        response.setCreatedAt(source.getCreateTime());
        response.setUpdatedAt(source.getUpdateTime());
        return response;
    }
}
