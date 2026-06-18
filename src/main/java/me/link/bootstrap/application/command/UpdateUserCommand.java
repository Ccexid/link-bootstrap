package me.link.bootstrap.application.command;

import me.link.bootstrap.domain.valueobject.StatusEnum;

import java.time.LocalDateTime;

/**
 * 更新用户命令对象，封装更新用户信息所需的数据。
 * <p>
 * 租户ID将从当前登录用户的上下文中自动获取，无需前端传入。
 * </p>
 */
public record UpdateUserCommand(Long id, String username, String password, String nickname, Integer userType, String mobile, String email, String avatar, StatusEnum status, Long orgId, Long deptId, String loginIp, LocalDateTime loginDate) {
}
