package me.link.bootstrap.application.command;

import me.link.bootstrap.shared.kernel.valueobject.SortingField;

import java.util.List;

/**
 * Query object carrying the criteria required to retrieve a paginated list of tenant packages.
 *
 * Encapsulates pagination settings, an optional tenant package name filter, and optional sorting
 * instructions for use by the tenant package application service when searching tenant packages.
 */
public record TenantPackagePageQuery(
        Integer pageNo,
        Integer pageSize,
        String name,
        List<SortingField> sortingFields
) {
}
