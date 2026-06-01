package me.link.bootstrap.application.command;

import java.time.LocalDateTime;
import java.util.Set;

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
