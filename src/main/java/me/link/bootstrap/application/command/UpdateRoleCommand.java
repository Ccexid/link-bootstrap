package me.link.bootstrap.application.command;

import me.link.bootstrap.domain.valueobject.StatusEnum;

/**
 * 更新角色命令对象，封装更新角色所需的数据。
 * <p>
 * 租户ID将从当前登录用户的上下文中自动获取，无需前端传入。
 * </p>
 */
public record UpdateRoleCommand(Long id, String name, String code, Integer sort, Integer dataScope, String dataScopeDeptIds, StatusEnum status, Integer type, String remark) {
}
