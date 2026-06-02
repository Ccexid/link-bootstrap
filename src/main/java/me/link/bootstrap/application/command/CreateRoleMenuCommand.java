package me.link.bootstrap.application.command;

import java.time.LocalDateTime;

public record CreateRoleMenuCommand(Long roleId, Long menuId, Long tenantId) {
}
