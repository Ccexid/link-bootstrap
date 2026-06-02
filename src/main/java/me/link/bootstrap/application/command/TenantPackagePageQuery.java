package me.link.bootstrap.application.command;

import me.link.bootstrap.shared.kernel.valueobject.SortingField;

import java.util.List;

public record TenantPackagePageQuery(
        Integer pageNo,
        Integer pageSize,
        String name,
        List<SortingField> sortingFields
) {
}
