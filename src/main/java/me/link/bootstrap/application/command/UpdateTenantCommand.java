package me.link.bootstrap.application.command;

import java.time.LocalDateTime;
import java.util.Set;

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
