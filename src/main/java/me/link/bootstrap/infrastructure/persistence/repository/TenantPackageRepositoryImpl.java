package me.link.bootstrap.infrastructure.persistence.repository;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.domain.entity.TenantPackageEntity;
import me.link.bootstrap.domain.repository.TenantPackageRepository;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.infrastructure.persistence.converter.TenantPackageConverter;
import me.link.bootstrap.infrastructure.persistence.internal.TenantPackageInternalService;
import me.link.bootstrap.infrastructure.persistence.po.TenantPackagePO;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TenantPackageRepositoryImpl implements TenantPackageRepository {

    private static final Map<String, String> SORT_FIELD_MAPPING = Map.of(
            "id", "id",
            "name", "name",
            "created_at", "create_time",
            "updated_at", "update_time"
    );

    private final TenantPackageInternalService tenantPackageInternalService;
    private final TenantPackageConverter tenantPackageConverter;

    @Override
    public TenantPackageEntity save(TenantPackageEntity tenantPackage) {
        TenantPackagePO tenantPackagePO = tenantPackageConverter.convert(tenantPackage);
        tenantPackageInternalService.save(tenantPackagePO);
        return tenantPackageConverter.reverseConvert(tenantPackagePO);
    }

    @Override
    public boolean update(TenantPackageEntity tenantPackage) {
        TenantPackagePO tenantPackagePO = tenantPackageConverter.convert(tenantPackage);
        return tenantPackageInternalService.updateById(tenantPackagePO);
    }

    @Override
    public Optional<TenantPackageEntity> findById(Long id) {
        return Optional.ofNullable(tenantPackageInternalService.getById(id))
                .map(tenantPackageConverter::reverseConvert);
    }

    @Override
    public PageResult<TenantPackageEntity> page(Integer pageNo, Integer pageSize, String name, List<SortingField> sortingFields) {
        Page<TenantPackagePO> page = Page.of(pageNo, pageSize);
        applyOrders(page, sortingFields);
        LambdaQueryWrapper<TenantPackagePO> wrapper = new LambdaQueryWrapper<TenantPackagePO>()
                .like(StrUtil.isNotBlank(name), TenantPackagePO::getName, name)
                .orderByDesc(sortingFields == null || sortingFields.isEmpty(), TenantPackagePO::getId);
        Page<TenantPackagePO> result = tenantPackageInternalService.page(page, wrapper);
        return new PageResult<>(tenantPackageConverter.reverseConvertList(result.getRecords()), result.getTotal());
    }

    @Override
    public boolean deleteById(Long id) {
        return tenantPackageInternalService.removeById(id);
    }

    private void applyOrders(Page<TenantPackagePO> page, List<SortingField> sortingFields) {
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
