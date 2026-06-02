package me.link.bootstrap.application.command;

import java.time.LocalDateTime;

public record UpdateUserRoleCommand(Long id, Long userId, Long roleId, Long tenantId) {
}
