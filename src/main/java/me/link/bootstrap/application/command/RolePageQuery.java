package me.link.bootstrap.application.command;

import me.link.bootstrap.domain.valueobject.StatusEnum;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;

import java.util.List;

/**
 * 角色分页查询对象，封装获取分页角色列表所需的查询条件。
 * <p>
 * 租户ID将从当前登录用户的上下文中自动获取，无需前端传入。
 * </p>
 */
public record RolePageQuery(Integer pageNo, Integer pageSize, String name, String code, StatusEnum status, Integer type, List<SortingField> sortingFields) {
}
