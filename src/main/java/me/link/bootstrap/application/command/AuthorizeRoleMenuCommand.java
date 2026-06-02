package me.link.bootstrap.application.command;

import java.util.List;

public record AuthorizeRoleMenuCommand(Long roleId, List<Long> menuIds, Long tenantId) {
}
