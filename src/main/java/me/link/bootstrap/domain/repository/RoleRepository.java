package me.link.bootstrap.domain.repository;

import me.link.bootstrap.domain.valueobject.StatusEnum;

import me.link.bootstrap.domain.entity.RoleEntity;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;

import java.util.List;
import java.util.Optional;

public interface RoleRepository {

    RoleEntity save(RoleEntity role);

    boolean update(RoleEntity role);

    Optional<RoleEntity> findById(Long id);

    PageResult<RoleEntity> page(Integer pageNo, Integer pageSize, String name, String code, StatusEnum status, Integer type, Long tenantId, List<SortingField> sortingFields);

    boolean deleteById(Long id);
}
