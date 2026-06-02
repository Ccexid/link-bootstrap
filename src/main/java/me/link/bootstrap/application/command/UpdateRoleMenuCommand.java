package me.link.bootstrap.application.command;

/**
 * 更新角色菜单关联命令对象，封装更新角色-菜单权限所需的数据。
 * <p>
 * 租户ID将从当前登录用户的上下文中自动获取，无需前端传入。
 * </p>
 */
public record UpdateRoleMenuCommand(Long id, Long roleId, Long menuId) {
}
