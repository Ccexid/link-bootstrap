package me.link.bootstrap.application.command;

import java.util.Set;

public record UpdateTenantPackageCommand(
        Long id,
        String name,
        String remark,
        Set<Long> menuIds,
        Boolean enabled
) {
}
