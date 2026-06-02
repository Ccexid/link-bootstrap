package me.link.bootstrap.application.command;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Command object carrying the data required to create a tenant.
 *
 * Encapsulates tenant identity, contact information, associated websites,
 * package assignment, expiration time, and account allocation for use by the
 * tenant application service during tenant creation.
 */
public record CreateTenantCommand(
        String name,
        Long contactUserId,
        String contactName,
        String contactMobile,
        Set<String> websites,
        Long packageId,
        LocalDateTime expireTime,
        Integer accountCount
) {
}
