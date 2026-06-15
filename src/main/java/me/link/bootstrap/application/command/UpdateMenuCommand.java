package me.link.bootstrap.application.command;

import me.link.bootstrap.domain.valueobject.StatusEnum;

import java.time.LocalDateTime;

/**
 * 更新菜单命令对象，封装菜单基础信息、路由配置和展示状态的变更值。
 */
public record UpdateMenuCommand(Long id, String name, String permission, Integer type, Integer sort, Long parentId, String path, String icon, String component, String componentName, StatusEnum status, Boolean visible, Boolean keepAlive, Boolean alwaysShow) {
}
