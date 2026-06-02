package me.link.bootstrap.application.command;

import me.link.bootstrap.domain.valueobject.StatusEnum;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;

import java.util.List;

public record RolePageQuery(Integer pageNo, Integer pageSize, String name, String code, StatusEnum status, Integer type, Long tenantId, java.util.List<me.link.bootstrap.shared.kernel.valueobject.SortingField> sortingFields) {
}
