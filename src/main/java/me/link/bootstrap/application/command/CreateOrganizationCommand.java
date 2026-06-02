package me.link.bootstrap.application.command;

import me.link.bootstrap.domain.valueobject.StatusEnum;

/**
 * 创建组织命令对象，封装创建新组织所需的数据。
 * <p>
 * 租户ID将从当前登录用户的上下文中自动获取，无需前端传入。
 * </p>
 */
public record CreateOrganizationCommand(String name, Integer orgType, Long parentId, String ancestors, Integer level, String contactName, String contactMobile, StatusEnum status) {
}
