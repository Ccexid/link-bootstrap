package me.link.bootstrap.application.command;

import me.link.bootstrap.shared.kernel.valueobject.SortingField;

import java.util.List;

/**
 * 用户角色关联分页查询对象，封装获取分页用户-角色关联列表所需的查询条件。
 * <p>
 * 租户ID将从当前登录用户的上下文中自动获取，无需前端传入。
 * </p>
 */
public record UserRolePageQuery(Integer pageNo, Integer pageSize, Long userId, Long roleId, List<SortingField> sortingFields) {
}
