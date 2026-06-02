package me.link.bootstrap.application.command;

import java.util.Set;

/**
 * Command object carrying the data required to create a tenant package.
 *
 * Encapsulates the tenant package name, descriptive remark, and associated menu
 * identifiers for use by the tenant package application service during tenant
 * package creation.
 */
public record CreateTenantPackageCommand(
        String name,
        String remark,
        Set<Long> menuIds
) {
}
