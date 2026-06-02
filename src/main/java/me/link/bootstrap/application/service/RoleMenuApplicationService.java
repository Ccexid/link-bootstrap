package me.link.bootstrap.application.service;

import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.command.AuthorizeRoleMenuCommand;
import me.link.bootstrap.application.command.CreateRoleMenuCommand;
import me.link.bootstrap.application.command.RoleMenuPageQuery;
import me.link.bootstrap.application.command.UpdateRoleMenuCommand;
import me.link.bootstrap.domain.entity.RoleMenuEntity;
import me.link.bootstrap.domain.factory.RoleMenuFactory;
import me.link.bootstrap.domain.repository.RoleMenuRepository;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import me.link.bootstrap.shared.kernel.util.SecurityHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色菜单关联应用服务，负责编排角色-菜单权限的增删改查和批量授权流程。
 * <p>
 * 租户ID从当前登录用户的上下文中自动获取，确保数据隔离安全性。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class RoleMenuApplicationService {

    private final RoleMenuRepository roleMenuRepository;

    /**
     * 创建角色菜单关联。
     * <p>
     * 租户ID从当前登录用户的上下文中自动获取。
     * </p>
     */
    @Transactional
    public RoleMenuEntity create(CreateRoleMenuCommand command) {
        Long tenantId = SecurityHelper.getTenantId();
        RoleMenuEntity roleMenu = RoleMenuFactory.create(command.roleId(), command.menuId(), tenantId);
        return roleMenuRepository.save(roleMenu);
    }

    /**
     * 根据主键查询角色菜单关联详情。
     */
    public RoleMenuEntity get(Long id) {
        return roleMenuRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_MENU_NOT_FOUND));
    }

    /**
     * 分页查询角色菜单关联列表。
     * <p>
     * 租户ID从当前登录用户的上下文中自动获取。
     * </p>
     */
    public PageResult<RoleMenuEntity> page(RoleMenuPageQuery query) {
        Long tenantId = SecurityHelper.getTenantId();
        return roleMenuRepository.page(query.pageNo(), query.pageSize(), query.roleId(), query.menuId(), tenantId, query.sortingFields());
    }

    /**
     * 更新角色菜单关联信息。
     * <p>
     * 租户ID从当前登录用户的上下文中自动获取。
     * </p>
     */
    @Transactional
    public RoleMenuEntity update(UpdateRoleMenuCommand command) {
        RoleMenuEntity roleMenu = get(command.id());
        Long tenantId = SecurityHelper.getTenantId();
        RoleMenuFactory.changeBasicInfo(roleMenu, command.roleId(), command.menuId(), tenantId);
        boolean updated = roleMenuRepository.update(roleMenu);
        if (!updated) {
            throw new BusinessException(ErrorCode.ROLE_MENU_NOT_FOUND);
        }
        return get(command.id());
    }

    /**
     * 批量授权角色菜单（覆盖式）。
     * <p>
     * 租户ID从当前登录用户的上下文中自动获取。
     * 先删除该角色在该租户下的所有旧菜单关联，再批量插入新的菜单关联。
     * </p>
     */
    @Transactional
    public void authorize(AuthorizeRoleMenuCommand command) {
        if (command.roleId() == null || command.roleId() <= 0) {
            throw new IllegalArgumentException("角色菜单关联roleId必须大于0");
        }
        Long tenantId = SecurityHelper.getTenantId();
        List<RoleMenuEntity> roleMenus = command.menuIds() == null ? List.of() : command.menuIds().stream()
                .distinct()
                .map(menuId -> RoleMenuFactory.create(command.roleId(), menuId, tenantId))
                .toList();
        roleMenuRepository.authorize(command.roleId(), tenantId, roleMenus);
    }

    /**
     * 删除角色菜单关联。
     */
    @Transactional
    public void delete(Long id) {
        if (!roleMenuRepository.deleteById(id)) {
            throw new BusinessException(ErrorCode.ROLE_MENU_NOT_FOUND);
        }
    }
}
