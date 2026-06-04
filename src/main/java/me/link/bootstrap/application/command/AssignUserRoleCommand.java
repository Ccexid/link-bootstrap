package me.link.bootstrap.application.command;

import java.util.List;

/**
 * 批量分配用户角色命令对象，封装用户-角色关联关系。
 * <p>
 * 租户ID将从当前登录用户的上下文中自动获取，无需前端传入。
 * </p>
 */
public record AssignUserRoleCommand(
        Long userId,
        List<Long> roleIds
) {
}
