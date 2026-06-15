package me.link.bootstrap.infrastructure.persistence.repository;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.domain.entity.TenantPackageEntity;
import me.link.bootstrap.domain.repository.TenantPackageRepository;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.infrastructure.persistence.converter.TenantPackageConverter;
import me.link.bootstrap.infrastructure.persistence.internal.TenantPackageInternalService;
import me.link.bootstrap.infrastructure.persistence.po.TenantPackagePO;
import me.link.bootstrap.infrastructure.persistence.repository.support.PageOrderHelper;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 租户套餐仓储实现，负责将领域仓储抽象适配到 MyBatis-Plus 持久化能力。
 */
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

    /**
     * 保存领域对象并返回持久化后的领域对象。
     */
    @Override
    public TenantPackageEntity save(TenantPackageEntity tenantPackage) {
        TenantPackagePO tenantPackagePO = tenantPackageConverter.convert(tenantPackage);
        tenantPackageInternalService.save(tenantPackagePO);
        return tenantPackageConverter.reverseConvert(tenantPackagePO);
    }

    /**
     * 更新业务对象。
     */
    @Override
    public boolean update(TenantPackageEntity tenantPackage) {
        TenantPackagePO tenantPackagePO = tenantPackageConverter.convert(tenantPackage);
        return tenantPackageInternalService.updateById(tenantPackagePO);
    }

    /**
     * 根据主键查询领域对象。
     */
    @Override
    public Optional<TenantPackageEntity> findById(Long id) {
        return Optional.ofNullable(tenantPackageInternalService.getById(id))
                .map(tenantPackageConverter::reverseConvert);
    }

    /**
     * 分页查询业务对象列表。
     */
    @Override
    public PageResult<TenantPackageEntity> page(Integer pageNo, Integer pageSize, String name, List<SortingField> sortingFields) {
        Page<TenantPackagePO> page = Page.of(pageNo, pageSize);
        PageOrderHelper.applyOrders(page, sortingFields, SORT_FIELD_MAPPING);
        LambdaQueryWrapper<TenantPackagePO> wrapper = new LambdaQueryWrapper<TenantPackagePO>()
                .like(StrUtil.isNotBlank(name), TenantPackagePO::getName, name)
                .orderByDesc(sortingFields == null || sortingFields.isEmpty(), TenantPackagePO::getId);
        Page<TenantPackagePO> result = tenantPackageInternalService.page(page, wrapper);
        return new PageResult<>(tenantPackageConverter.reverseConvertList(result.getRecords()), result.getTotal());
    }

    /**
     * 根据主键删除领域对象。
     */
    @Override
    public boolean deleteById(Long id) {
        return tenantPackageInternalService.removeById(id);
    }

}
