package me.link.bootstrap.application.command;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Command object carrying the data required to update an existing tenant.
 *
 * Encapsulates the tenant identifier, contact information, associated websites,
 * package assignment, expiration time, account allocation, and enabled state for
 * use by the tenant application service during tenant updates.
 */
public record UpdateTenantCommand(
        Long id,
        String contactName,
        Long contactUserId,
        String contactMobile,
        Set<String> websites,
        Long packageId,
        LocalDateTime expireTime,
        Integer accountCount,
        Boolean enabled
) {
}
