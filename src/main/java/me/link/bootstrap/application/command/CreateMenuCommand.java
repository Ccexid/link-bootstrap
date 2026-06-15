package me.link.bootstrap.application.command;

import me.link.bootstrap.domain.valueobject.StatusEnum;

/**
 * 创建菜单命令对象，封装菜单基础信息、路由配置和展示状态。
 */
public record CreateMenuCommand(String name, String permission, Integer type, Integer sort, Long parentId, String path,
                                String icon, String component, String componentName, StatusEnum status, Boolean visible,
                                Boolean keepAlive, Boolean alwaysShow) {
}
