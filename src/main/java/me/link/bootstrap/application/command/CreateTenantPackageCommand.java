package me.link.bootstrap.application.command;

import java.util.Set;

public record CreateTenantPackageCommand(
        String name,
        String remark,
        Set<Long> menuIds
) {
}
