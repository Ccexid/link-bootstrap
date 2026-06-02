package me.link.bootstrap.domain.repository;

import me.link.bootstrap.domain.entity.OperateLogEntity;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;

import java.util.List;
import java.util.Optional;

public interface OperateLogRepository {

    OperateLogEntity save(OperateLogEntity operateLog);

    boolean update(OperateLogEntity operateLog);

    Optional<OperateLogEntity> findById(Long id);

    PageResult<OperateLogEntity> page(Integer pageNo, Integer pageSize, String traceId, Long userId, String module, Integer operation, Long bizId, Boolean success, Long tenantId, List<SortingField> sortingFields);

    boolean deleteById(Long id);
}
