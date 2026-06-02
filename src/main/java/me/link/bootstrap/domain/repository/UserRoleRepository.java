package me.link.bootstrap.domain.repository;

import me.link.bootstrap.domain.entity.UserRoleEntity;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;

import java.util.List;
import java.util.Optional;

public interface UserRoleRepository {

    UserRoleEntity save(UserRoleEntity userRole);

    boolean update(UserRoleEntity userRole);

    Optional<UserRoleEntity> findById(Long id);

    PageResult<UserRoleEntity> page(Integer pageNo, Integer pageSize, Long userId, Long roleId, Long tenantId, List<SortingField> sortingFields);

    boolean deleteById(Long id);

    void assign(Long userId, Long tenantId, List<UserRoleEntity> userRoles);
}
