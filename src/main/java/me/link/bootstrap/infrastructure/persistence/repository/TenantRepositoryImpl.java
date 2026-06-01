package me.link.bootstrap.infrastructure.persistence.repository;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.domain.entity.TenantEntity;
import me.link.bootstrap.domain.repository.TenantRepository;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.infrastructure.persistence.converter.TenantConverter;
import me.link.bootstrap.infrastructure.persistence.internal.TenantInternalService;
import me.link.bootstrap.infrastructure.persistence.po.TenantPO;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TenantRepositoryImpl implements TenantRepository {

    private static final Map<String, String> SORT_FIELD_MAPPING = Map.of(
            "id", "id",
            "name", "name",
            "created_at", "create_time",
            "updated_at", "update_time"
    );

    private final TenantInternalService tenantInternalService;
    private final TenantConverter tenantConverter;

    @Override
    public TenantEntity save(TenantEntity tenant) {
        TenantPO tenantPO = tenantConverter.convert(tenant);
        tenantInternalService.save(tenantPO);
        return tenantConverter.reverseConvert(tenantPO);
    }

    @Override
    public boolean update(TenantEntity tenant) {
        TenantPO tenantPO = tenantConverter.convert(tenant);
        return tenantInternalService.updateById(tenantPO);
    }

    @Override
    public Optional<TenantEntity> findById(Long id) {
        return Optional.ofNullable(tenantInternalService.getById(id))
                .map(tenantConverter::reverseConvert);
    }

    @Override
    public PageResult<TenantEntity> page(Integer pageNo, Integer pageSize, String name, List<SortingField> sortingFields) {
        Page<TenantPO> page = Page.of(pageNo, pageSize);
        applyOrders(page, sortingFields);
        LambdaQueryWrapper<TenantPO> wrapper = new LambdaQueryWrapper<TenantPO>()
                .like(StrUtil.isNotBlank(name), TenantPO::getName, name)
                .orderByDesc(sortingFields == null || sortingFields.isEmpty(), TenantPO::getId);
        Page<TenantPO> result = tenantInternalService.page(page, wrapper);
        return new PageResult<>(tenantConverter.reverseConvertList(result.getRecords()), result.getTotal());
    }

    @Override
    public boolean deleteById(Long id) {
        return tenantInternalService.removeById(id);
    }

    private void applyOrders(Page<TenantPO> page, List<SortingField> sortingFields) {
        if (sortingFields == null || sortingFields.isEmpty()) {
            return;
        }
        sortingFields.stream()
                .map(this::toOrderItem)
                .forEach(page::addOrder);
    }

    private OrderItem toOrderItem(SortingField sortingField) {
        String column = SORT_FIELD_MAPPING.get(sortingField.getField());
        if (sortingField.isAsc()) {
            return OrderItem.asc(column);
        }
        return OrderItem.desc(column);
    }
}
