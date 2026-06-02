package me.link.bootstrap.application.command;

import me.link.bootstrap.shared.kernel.valueobject.SortingField;

import java.util.List;

/**
 * 租户分页查询对象，封装获取分页租户列表所需的查询条件。
 *
 * 包含分页设置、可选的租户名称过滤条件和排序指令，
 * 供租户应用服务在搜索租户时使用。
 */
public record TenantPageQuery(
        Integer pageNo,
        Integer pageSize,
        String name,
        List<SortingField> sortingFields
) {
}
