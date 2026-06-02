package me.link.bootstrap.domain.repository;

import me.link.bootstrap.domain.entity.TenantPackageEntity;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;

import java.util.List;
import java.util.Optional;

/**
 * 租户套餐仓储接口，定义领域层访问套餐持久化数据所需的抽象能力。
 */
public interface TenantPackageRepository {

    TenantPackageEntity save(TenantPackageEntity tenantPackage);

    boolean update(TenantPackageEntity tenantPackage);

    Optional<TenantPackageEntity> findById(Long id);

    PageResult<TenantPackageEntity> page(Integer pageNo, Integer pageSize, String name, List<SortingField> sortingFields);

    boolean deleteById(Long id);
}
