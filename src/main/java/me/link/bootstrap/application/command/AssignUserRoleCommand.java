package me.link.bootstrap.application.command;

import java.util.List;

public record AssignUserRoleCommand(Long userId, List<Long> roleIds, Long tenantId) {
}
