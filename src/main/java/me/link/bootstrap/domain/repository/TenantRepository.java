package me.link.bootstrap.domain.repository;

import me.link.bootstrap.domain.entity.TenantEntity;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;

import java.util.List;
import java.util.Optional;

public interface TenantRepository {

    TenantEntity save(TenantEntity tenant);

    boolean update(TenantEntity tenant);

    Optional<TenantEntity> findById(Long id);

    PageResult<TenantEntity> page(Integer pageNo, Integer pageSize, String name, List<SortingField> sortingFields);

    boolean deleteById(Long id);
}
