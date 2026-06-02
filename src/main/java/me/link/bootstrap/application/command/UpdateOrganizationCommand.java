package me.link.bootstrap.application.command;

import me.link.bootstrap.domain.valueobject.StatusEnum;

import java.time.LocalDateTime;

public record UpdateOrganizationCommand(Long id, String name, Integer orgType, Long parentId, String ancestors, Integer level, String contactName, String contactMobile, StatusEnum status, Long tenantId) {
}
