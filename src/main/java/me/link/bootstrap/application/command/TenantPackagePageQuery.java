package me.link.bootstrap.application.command;

import me.link.bootstrap.shared.kernel.valueobject.SortingField;

import java.util.List;

/**
 * 租户套餐分页查询对象，封装获取分页租户套餐列表所需的查询条件。
 *
 * 包含分页设置、可选的套餐名称过滤条件和排序指令，
 * 供租户套餐应用服务在搜索套餐时使用。
 */
public record TenantPackagePageQuery(
        Integer pageNo,
        Integer pageSize,
        String name,
        List<SortingField> sortingFields
) {
}
