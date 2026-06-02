package me.link.bootstrap.application.command;

/**
 * 创建角色菜单关联命令对象，封装单个角色-菜单权限关系。
 * <p>
 * 租户ID将从当前登录用户的上下文中自动获取，无需前端传入。
 * </p>
 */
public record CreateRoleMenuCommand(Long roleId, Long menuId) {
}
