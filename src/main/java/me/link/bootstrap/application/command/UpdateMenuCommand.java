package me.link.bootstrap.application.command;

import me.link.bootstrap.domain.valueobject.StatusEnum;

import java.time.LocalDateTime;

public record UpdateMenuCommand(Long id, String name, String permission, Integer type, Integer sort, Long parentId, String path, String icon, String component, String componentName, StatusEnum status, Boolean visible, Boolean keepAlive, Boolean alwaysShow) {
}
