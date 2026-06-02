package me.link.bootstrap.domain.repository;

import me.link.bootstrap.domain.entity.RoleMenuEntity;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;

import java.util.List;
import java.util.Optional;

public interface RoleMenuRepository {

    RoleMenuEntity save(RoleMenuEntity roleMenu);

    boolean update(RoleMenuEntity roleMenu);

    Optional<RoleMenuEntity> findById(Long id);

    PageResult<RoleMenuEntity> page(Integer pageNo, Integer pageSize, Long roleId, Long menuId, Long tenantId, List<SortingField> sortingFields);

    boolean deleteById(Long id);
}
