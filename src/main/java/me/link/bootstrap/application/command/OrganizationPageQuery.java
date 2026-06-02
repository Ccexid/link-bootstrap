package me.link.bootstrap.application.command;

import me.link.bootstrap.domain.valueobject.StatusEnum;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;

import java.util.List;

public record OrganizationPageQuery(Integer pageNo, Integer pageSize, String name, Integer orgType, Long parentId, StatusEnum status, Long tenantId, java.util.List<me.link.bootstrap.shared.kernel.valueobject.SortingField> sortingFields) {
}
