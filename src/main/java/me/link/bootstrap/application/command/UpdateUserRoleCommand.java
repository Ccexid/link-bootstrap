package me.link.bootstrap.application.command;

/**
 * 更新用户角色关联命令对象，封装更新用户-角色关联所需的数据。
 * <p>
 * 租户ID将从当前登录用户的上下文中自动获取，无需前端传入。
 * </p>
 */
public record UpdateUserRoleCommand(Long id, Long userId, Long roleId) {
}
