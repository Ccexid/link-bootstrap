package me.link.bootstrap.application.command;

import me.link.bootstrap.shared.kernel.valueobject.SortingField;

import java.util.List;

/**
 * 角色菜单关联分页查询对象，封装获取分页角色-菜单权限列表所需的查询条件。
 * <p>
 * 租户ID将从当前登录用户的上下文中自动获取，无需前端传入。
 * </p>
 */
public record RoleMenuPageQuery(Integer pageNo, Integer pageSize, Long roleId, Long menuId, List<SortingField> sortingFields) {
}
