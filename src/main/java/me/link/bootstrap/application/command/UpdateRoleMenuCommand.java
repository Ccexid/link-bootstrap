package me.link.bootstrap.application.command;

import java.time.LocalDateTime;

public record UpdateRoleMenuCommand(Long id, Long roleId, Long menuId, Long tenantId) {
}
