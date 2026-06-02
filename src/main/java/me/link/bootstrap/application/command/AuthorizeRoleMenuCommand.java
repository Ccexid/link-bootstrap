package me.link.bootstrap.application.command;

import java.util.List;

/**
 * 批量授权角色菜单命令对象，封装角色-菜单权限关系。
 * <p>
 * 租户ID将从当前登录用户的上下文中自动获取，无需前端传入。
 * </p>
 */
public record AuthorizeRoleMenuCommand(Long roleId, List<Long> menuIds) {
}
