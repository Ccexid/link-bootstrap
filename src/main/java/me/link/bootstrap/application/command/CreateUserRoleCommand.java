package me.link.bootstrap.application.command;

/**
 * 创建用户角色关联命令对象，封装单个用户-角色关联关系。
 * <p>
 * 租户ID将从当前登录用户的上下文中自动获取，无需前端传入。
 * </p>
 */
public record CreateUserRoleCommand(Long userId, Long roleId) {
}
