package me.link.bootstrap.application.service.impl;

import cn.hutool.core.bean.BeanUtil;
import me.link.bootstrap.application.service.RoleMenuService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.infrastructure.mapper.RoleMenuMapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.support.ApplicationAssert;
import me.link.bootstrap.application.service.MenuService;
import me.link.bootstrap.application.service.RoleService;
import me.link.bootstrap.infrastructure.persistence.po.RolePO;
import me.link.bootstrap.infrastructure.persistence.po.RoleMenuPO;
import me.link.bootstrap.interfaces.dto.response.vo.RoleMenuResponseVO;
import me.link.bootstrap.infrastructure.persistence.support.PageOrderHelper;
import me.link.bootstrap.infrastructure.security.PermissionCacheService;
import me.link.bootstrap.interfaces.dto.request.rolemenu.RoleMenuAuthorizeRequest;
import me.link.bootstrap.interfaces.dto.request.rolemenu.RoleMenuCreateRequest;
import me.link.bootstrap.interfaces.dto.request.rolemenu.RoleMenuPageRequest;
import me.link.bootstrap.interfaces.dto.request.rolemenu.RoleMenuUpdateRequest;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import me.link.bootstrap.shared.kernel.util.SecurityHelper;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 角色菜单关联服务，直接编排角色-菜单权限增删改查、覆盖式授权和权限缓存失效。
 * <p>
 * 多租户隔离由 {@code TenantLineInnerInterceptor} 全局处理：所有针对角色菜单表
 * 的 SELECT/UPDATE/DELETE 自动追加 {@code tenant_id = ?} 条件，规避水平越权（IDOR）。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenuPO> implements RoleMenuService {

    private static final Map<String, String> SORT_FIELD_MAPPING = Map.of(
            "id", "id",
            "created_at", "create_time",
            "updated_at", "update_time",
            "role_id", "role_id",
            "menu_id", "menu_id",
            "tenant_id", "tenant_id"
    );
    private final RoleService roleService;
    private final MenuService menuService;
    private final PermissionCacheService permissionCacheService;

    @Transactional
    public RoleMenuResponseVO create(RoleMenuCreateRequest request) {
        Long tenantId = SecurityHelper.getRequiredTenantId();
        validateReferences(request.getRoleId(), request.getMenuId(), tenantId);
        validateUniqueBinding(request.getRoleId(), request.getMenuId(), tenantId, null);
        RoleMenuPO roleMenu = createPO(request.getRoleId(), request.getMenuId(), tenantId);
        save(roleMenu);
        permissionCacheService.evictByRoleId(request.getRoleId());
        return toResponse(roleMenu);
    }

    public RoleMenuResponseVO get(Long id) {
        return toResponse(getRequired(id));
    }

    public PageResult<RoleMenuResponseVO> page(RoleMenuPageRequest request) {
        Page<RoleMenuPO> page = Page.of(request.getPageNo(), request.getPageSize());
        PageOrderHelper.applyOrders(page, request.getSortingFields(), SORT_FIELD_MAPPING);
        LambdaQueryWrapper<RoleMenuPO> wrapper = new LambdaQueryWrapper<RoleMenuPO>()
                .eq(request.getRoleId() != null, RoleMenuPO::getRoleId, request.getRoleId())
                .eq(request.getMenuId() != null, RoleMenuPO::getMenuId, request.getMenuId())
                .orderByDesc(request.getSortingFields() == null || request.getSortingFields().isEmpty(), RoleMenuPO::getId);
        Page<RoleMenuPO> result = page(page, wrapper);
        return new PageResult<>(result.getRecords().stream().map(this::toResponse).toList(), result.getTotal());
    }

    @Transactional
    public RoleMenuResponseVO update(Long id, RoleMenuUpdateRequest request) {
        RoleMenuPO roleMenu = getRequired(id);
        Long tenantId = SecurityHelper.getRequiredTenantId();
        Long oldRoleId = roleMenu.getRoleId();
        validateReferences(request.getRoleId(), request.getMenuId(), tenantId);
        validateUniqueBinding(request.getRoleId(), request.getMenuId(), tenantId, id);
        applyMutableFields(roleMenu, request.getRoleId(), request.getMenuId(), tenantId);
        ApplicationAssert.requireSuccess(updateById(roleMenu), ErrorCode.ROLE_MENU_NOT_FOUND);
        // 旧/新 roleId 都失效,避免改了关联后老 role 的缓存仍含旧菜单
        permissionCacheService.evictByRoleId(oldRoleId);
        if (!oldRoleId.equals(request.getRoleId())) {
            permissionCacheService.evictByRoleId(request.getRoleId());
        }
        return get(id);
    }

    /**
     * 批量授权角色菜单（覆盖式）。
     * <p>
     * 先删除该角色在该租户下的所有旧菜单关联，再批量插入新的菜单关联。
     * </p>
     */
    @Transactional
    public void authorize(RoleMenuAuthorizeRequest request) {
        if (request.getRoleId() == null || request.getRoleId() <= 0) {
            ApplicationAssert.invalidParam("角色菜单关联roleId必须大于0");
        }
        Long tenantId = SecurityHelper.getRequiredTenantId();
        List<Long> menuIds = request.getMenuIds() == null ? List.of() : request.getMenuIds().stream()
                .distinct()
                .toList();
        validateRoleInCurrentTenant(request.getRoleId(), tenantId);
        validateMenuIds(menuIds);
        List<RoleMenuPO> roleMenus = menuIds.stream()
                .map(menuId -> createPO(request.getRoleId(), menuId, tenantId))
                .toList();
        remove(new LambdaQueryWrapper<RoleMenuPO>()
                .eq(RoleMenuPO::getRoleId, request.getRoleId())
                .eq(RoleMenuPO::getTenantId, tenantId));
        if (!roleMenus.isEmpty()) {
            saveBatch(roleMenus);
        }
        permissionCacheService.evictByRoleId(request.getRoleId());
    }

    @Transactional
    public void delete(Long id) {
        // 先 get 拿 roleId 用于 evict,再删除
        RoleMenuPO roleMenu = getRequired(id);
        ApplicationAssert.requireSuccess(removeById(id), ErrorCode.ROLE_MENU_NOT_FOUND);
        permissionCacheService.evictByRoleId(roleMenu.getRoleId());
    }

    private static RoleMenuPO createPO(Long roleId, Long menuId, Long tenantId) {
        RoleMenuPO roleMenu = new RoleMenuPO();
        applyMutableFields(roleMenu, roleId, menuId, tenantId);
        return roleMenu;
    }

    private void validateReferences(Long roleId, Long menuId, Long tenantId) {
        if (roleId == null || roleId <= 0) {
            ApplicationAssert.invalidParam("角色菜单关联roleId必须大于0");
        }
        if (menuId == null || menuId <= 0) {
            ApplicationAssert.invalidParam("角色菜单关联menuId必须大于0");
        }
        validateRoleInCurrentTenant(roleId, tenantId);
        validateMenuExists(menuId);
    }

    private void validateRoleInCurrentTenant(Long roleId, Long tenantId) {
        RolePO role = roleService.getById(roleId);
        if (role == null || !Objects.equals(role.getTenantId(), tenantId)) {
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
        }
    }

    private void validateMenuExists(Long menuId) {
        if (menuService.getById(menuId) == null) {
            throw new BusinessException(ErrorCode.MENU_NOT_FOUND);
        }
    }

    private void validateMenuIds(List<Long> menuIds) {
        for (Long menuId : menuIds) {
            if (menuId == null || menuId <= 0) {
                ApplicationAssert.invalidParam("角色菜单关联menuId必须大于0");
            }
            validateMenuExists(menuId);
        }
    }

    private void validateUniqueBinding(Long roleId, Long menuId, Long tenantId, Long excludeId) {
        boolean exists = exists(new LambdaQueryWrapper<RoleMenuPO>()
                .eq(RoleMenuPO::getRoleId, roleId)
                .eq(RoleMenuPO::getMenuId, menuId)
                .eq(RoleMenuPO::getTenantId, tenantId)
                .ne(excludeId != null, RoleMenuPO::getId, excludeId));
        if (exists) {
            ApplicationAssert.invalidParam("角色菜单关联已存在");
        }
    }

    private static void applyMutableFields(RoleMenuPO roleMenu, Long roleId, Long menuId, Long tenantId) {
        if (roleId == null || roleId <= 0) {
            ApplicationAssert.invalidParam("角色菜单关联roleId必须大于0");
        }
        if (menuId == null || menuId <= 0) {
            ApplicationAssert.invalidParam("角色菜单关联menuId必须大于0");
        }
        if (tenantId == null || tenantId <= 0) {
            ApplicationAssert.invalidParam("角色菜单关联tenantId必须大于0");
        }
        roleMenu.setRoleId(roleId);
        roleMenu.setMenuId(menuId);
        roleMenu.setTenantId(tenantId);
    }

    private RoleMenuPO getRequired(Long id) {
        return ApplicationAssert.requireFound(getById(id), ErrorCode.ROLE_MENU_NOT_FOUND);
    }

    private RoleMenuResponseVO toResponse(RoleMenuPO source) {
        RoleMenuResponseVO response = BeanUtil.copyProperties(source, RoleMenuResponseVO.class);
        response.setCreatedAt(source.getCreateTime());
        response.setUpdatedAt(source.getUpdateTime());
        return response;
    }
}
