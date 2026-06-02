package me.link.bootstrap.application.command;

import me.link.bootstrap.domain.valueobject.StatusEnum;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;

import java.util.List;

public record OperateLogPageQuery(Integer pageNo, Integer pageSize, String traceId, Long userId, String module, Integer operation, Long bizId, Boolean success, Long tenantId, java.util.List<me.link.bootstrap.shared.kernel.valueobject.SortingField> sortingFields) {
}
