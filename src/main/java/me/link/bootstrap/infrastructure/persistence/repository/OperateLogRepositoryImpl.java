package me.link.bootstrap.infrastructure.persistence.repository;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.domain.entity.OperateLogEntity;
import me.link.bootstrap.domain.repository.OperateLogRepository;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.infrastructure.persistence.converter.OperateLogConverter;
import me.link.bootstrap.infrastructure.persistence.internal.OperateLogInternalService;
import me.link.bootstrap.infrastructure.persistence.po.OperateLogPO;
import me.link.bootstrap.infrastructure.persistence.repository.support.PageOrderHelper;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OperateLogRepositoryImpl implements OperateLogRepository {

    private static final Map<String, String> SORT_FIELD_MAPPING = Map.of(
            "id", "id",
            "created_at", "create_time",
            "updated_at", "update_time",
            "user_id", "user_id",
            "biz_id", "biz_id",
            "tenant_id", "tenant_id"
    );

    private final OperateLogInternalService operateLogInternalService;
    private final OperateLogConverter operateLogConverter;

    @Override
    public OperateLogEntity save(OperateLogEntity operateLog) {
        OperateLogPO operateLogPO = operateLogConverter.convert(operateLog);
        operateLogInternalService.save(operateLogPO);
        return operateLogConverter.reverseConvert(operateLogPO);
    }

    @Override
    public boolean update(OperateLogEntity operateLog) {
        OperateLogPO operateLogPO = operateLogConverter.convert(operateLog);
        return operateLogInternalService.updateById(operateLogPO);
    }

    @Override
    public Optional<OperateLogEntity> findById(Long id) {
        return Optional.ofNullable(operateLogInternalService.getById(id))
                .map(operateLogConverter::reverseConvert);
    }

    @Override
    public PageResult<OperateLogEntity> page(Integer pageNo, Integer pageSize, String traceId, Long userId, String module, Integer operation, Long bizId, Boolean success, Long tenantId, List<SortingField> sortingFields) {
        Page<OperateLogPO> page = Page.of(pageNo, pageSize);
        PageOrderHelper.applyOrders(page, sortingFields, SORT_FIELD_MAPPING);
        LambdaQueryWrapper<OperateLogPO> wrapper = new LambdaQueryWrapper<OperateLogPO>()
                .like(StrUtil.isNotBlank(traceId), OperateLogPO::getTraceId, traceId)
                .eq(userId != null, OperateLogPO::getUserId, userId)
                .like(StrUtil.isNotBlank(module), OperateLogPO::getModule, module)
                .eq(operation != null, OperateLogPO::getOperation, operation)
                .eq(bizId != null, OperateLogPO::getBizId, bizId)
                .eq(success != null, OperateLogPO::getSuccess, success)
                .eq(tenantId != null, OperateLogPO::getTenantId, tenantId)
                .orderByDesc(sortingFields == null || sortingFields.isEmpty(), OperateLogPO::getId);
        Page<OperateLogPO> result = operateLogInternalService.page(page, wrapper);
        return new PageResult<>(operateLogConverter.reverseConvertList(result.getRecords()), result.getTotal());
    }

    @Override
    public boolean deleteById(Long id) {
        return operateLogInternalService.removeById(id);
    }

}
