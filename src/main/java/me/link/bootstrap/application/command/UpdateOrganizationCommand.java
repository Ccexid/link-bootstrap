package me.link.bootstrap.application.command;

import me.link.bootstrap.domain.valueobject.StatusEnum;

/**
 * 更新组织命令对象，封装更新组织所需的数据。
 * <p>
 * 租户ID将从当前登录用户的上下文中自动获取，无需前端传入。
 * </p>
 */
public record UpdateOrganizationCommand(Long id, String name, Integer orgType, Long parentId, String ancestors, Integer level, String contactName, String contactMobile, StatusEnum status) {
}
