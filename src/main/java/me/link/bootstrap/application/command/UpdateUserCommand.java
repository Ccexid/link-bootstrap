package me.link.bootstrap.application.command;

import me.link.bootstrap.domain.valueobject.StatusEnum;

import java.time.LocalDateTime;

public record UpdateUserCommand(Long id, String username, String password, String nickname, Integer userType, String mobile, String avatar, StatusEnum status, Long orgId, Long deptId, String loginIp, LocalDateTime loginDate, Long tenantId) {
}
