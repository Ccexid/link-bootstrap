package me.link.bootstrap.application.command;

import me.link.bootstrap.domain.valueobject.StatusEnum;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;

import java.util.List;

public record UserRolePageQuery(Integer pageNo, Integer pageSize, Long userId, Long roleId, Long tenantId, java.util.List<me.link.bootstrap.shared.kernel.valueobject.SortingField> sortingFields) {
}
