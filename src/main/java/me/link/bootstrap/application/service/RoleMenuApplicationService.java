package me.link.bootstrap.application.service;

import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.support.ApplicationAssert;
import me.link.bootstrap.application.command.AuthorizeRoleMenuCommand;
import me.link.bootstrap.application.command.CreateRoleMenuCommand;
import me.link.bootstrap.application.command.RoleMenuPageQuery;
import me.link.bootstrap.application.command.UpdateRoleMenuCommand;
import me.link.bootstrap.domain.entity.RoleMenuEntity;
import me.link.bootstrap.domain.factory.RoleMenuFactory;
import me.link.bootstrap.domain.repository.RoleMenuRepository;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.infrastructure.security.PermissionCacheService;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import me.link.bootstrap.shared.kernel.util.SecurityHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色菜单关联应用服务，负责编排角色-菜单权限的增删改查和批量授权流程。
 * <p>
 * 多租户隔离由 {@code TenantLineInnerInterceptor} 全局处理：所有针对角色菜单表
 * 的 SELECT/UPDATE/DELETE 自动追加 {@code tenant_id = ?} 条件，规避水平越权（IDOR）。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class RoleMenuApplicationService {

    private final RoleMenuRepository roleMenuRepository;
    private final PermissionCacheService permissionCacheService;

    /**
     * 创建角色菜单关联。
     */
    @Transactional
    public RoleMenuEntity create(CreateRoleMenuCommand command) {
        Long tenantId = SecurityHelper.getRequiredTenantId();
        RoleMenuEntity roleMenu = RoleMenuFactory.create(command.roleId(), command.menuId(), tenantId);
        RoleMenuEntity saved = roleMenuRepository.save(roleMenu);
        permissionCacheService.evictByRoleId(command.roleId());
        return saved;
    }

    /**
     * 根据主键查询角色菜单关联详情。
     */
    public RoleMenuEntity get(Long id) {
        return ApplicationAssert.requireFound(roleMenuRepository.findById(id), ErrorCode.ROLE_MENU_NOT_FOUND);
    }

    /**
     * 分页查询角色菜单关联列表。
     */
    public PageResult<RoleMenuEntity> page(RoleMenuPageQuery query) {
        return roleMenuRepository.page(query.pageNo(), query.pageSize(), query.roleId(), query.menuId(), null, query.sortingFields());
    }

    /**
     * 更新角色菜单关联信息。
     */
    @Transactional
    public RoleMenuEntity update(UpdateRoleMenuCommand command) {
        RoleMenuEntity roleMenu = get(command.id());
        Long tenantId = SecurityHelper.getRequiredTenantId();
        Long oldRoleId = roleMenu.getRoleId();
        RoleMenuFactory.changeBasicInfo(roleMenu, command.roleId(), command.menuId(), tenantId);
        ApplicationAssert.requireSuccess(roleMenuRepository.update(roleMenu), ErrorCode.ROLE_MENU_NOT_FOUND);
        // 旧/新 roleId 都失效,避免改了关联后老 role 的缓存仍含旧菜单
        permissionCacheService.evictByRoleId(oldRoleId);
        if (!oldRoleId.equals(command.roleId())) {
            permissionCacheService.evictByRoleId(command.roleId());
        }
        return get(command.id());
    }

    /**
     * 批量授权角色菜单（覆盖式）。
     * <p>
     * 先删除该角色在该租户下的所有旧菜单关联，再批量插入新的菜单关联。
     * </p>
     */
    @Transactional
    public void authorize(AuthorizeRoleMenuCommand command) {
        if (command.roleId() == null || command.roleId() <= 0) {
            throw new IllegalArgumentException("角色菜单关联roleId必须大于0");
        }
        Long tenantId = SecurityHelper.getRequiredTenantId();
        List<RoleMenuEntity> roleMenus = command.menuIds() == null ? List.of() : command.menuIds().stream()
                .distinct()
                .map(menuId -> RoleMenuFactory.create(command.roleId(), menuId, tenantId))
                .toList();
        roleMenuRepository.authorize(command.roleId(), tenantId, roleMenus);
        permissionCacheService.evictByRoleId(command.roleId());
    }

    /**
     * 删除角色菜单关联。
     */
    @Transactional
    public void delete(Long id) {
        // 先 get 拿 roleId 用于 evict,再删除
        RoleMenuEntity roleMenu = get(id);
        ApplicationAssert.requireSuccess(roleMenuRepository.deleteById(id), ErrorCode.ROLE_MENU_NOT_FOUND);
        permissionCacheService.evictByRoleId(roleMenu.getRoleId());
    }
}
