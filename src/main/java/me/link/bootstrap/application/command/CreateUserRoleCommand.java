package me.link.bootstrap.application.command;

import java.time.LocalDateTime;

public record CreateUserRoleCommand(Long userId, Long roleId, Long tenantId) {
}
