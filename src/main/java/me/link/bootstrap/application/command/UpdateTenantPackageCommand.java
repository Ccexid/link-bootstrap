package me.link.bootstrap.application.command;

import java.util.Set;

/**
 * Command object carrying the data required to update an existing tenant package.
 *
 * Encapsulates the tenant package identifier, name, descriptive remark,
 * associated menu identifiers, and enabled state for use by the tenant package
 * application service during tenant package updates.
 */
public record UpdateTenantPackageCommand(
        Long id,
        String name,
        String remark,
        Set<Long> menuIds,
        Boolean enabled
) {
}
