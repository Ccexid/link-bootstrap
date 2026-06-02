package me.link.bootstrap.application.command;

import me.link.bootstrap.shared.kernel.valueobject.SortingField;

import java.util.List;

/**
 * Query object carrying the criteria required to retrieve a paginated list of tenants.
 *
 * Encapsulates pagination settings, an optional tenant name filter, and optional sorting
 * instructions for use by the tenant application service when searching tenants.
 */
public record TenantPageQuery(
        Integer pageNo,
        Integer pageSize,
        String name,
        List<SortingField> sortingFields
) {
}
