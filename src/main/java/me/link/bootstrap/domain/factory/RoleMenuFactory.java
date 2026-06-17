package me.link.bootstrap.domain.factory;

import me.link.bootstrap.domain.entity.RoleMenuEntity;

/**
 * 角色菜单关联领域工厂，集中封装角色授权关系的创建和变更校验。
 * <p>
 * 角色菜单授权隶属于当前租户，租户编号是授权边界的一部分；这里做最后一道参数校验，
 * 防止绕过应用服务时写入无租户归属的授权记录。
 * </p>
 */
public final class RoleMenuFactory {

    private RoleMenuFactory() {
        throw new UnsupportedOperationException("工厂类不允许实例化");
    }

    public static RoleMenuEntity create(Long roleId, Long menuId, Long tenantId) {
        validate(roleId, menuId, tenantId);
        return RoleMenuEntity.create(roleId, menuId, tenantId);
    }

    public static void changeBasicInfo(RoleMenuEntity roleMenu, Long roleId, Long menuId, Long tenantId) {
        if (roleMenu == null) {
            throw new IllegalArgumentException("角色菜单关联不能为空");
        }
        validate(roleId, menuId, tenantId);
        roleMenu.changeBasicInfo(roleId, menuId, tenantId);
    }

    private static void validate(Long roleId, Long menuId, Long tenantId) {
        if (roleId == null || roleId <= 0) {
            throw new IllegalArgumentException("角色菜单关联roleId必须大于0");
        }
        if (menuId == null || menuId <= 0) {
            throw new IllegalArgumentException("角色菜单关联menuId必须大于0");
        }
    }
}
