package me.link.bootstrap.domain.repository;

import me.link.bootstrap.domain.entity.UserRoleEntity;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;

import java.util.List;
import java.util.Optional;

/**
 * 用户角色关联仓储接口，定义领域层访问用户授权关系所需的抽象能力。
 */
public interface UserRoleRepository {

    UserRoleEntity save(UserRoleEntity userRole);

    boolean update(UserRoleEntity userRole);

    Optional<UserRoleEntity> findById(Long id);

    PageResult<UserRoleEntity> page(Integer pageNo, Integer pageSize, Long userId, Long roleId, Long tenantId, List<SortingField> sortingFields);

    boolean deleteById(Long id);

    /**
     * 覆盖式保存指定用户在当前租户下的角色分配关系。
     */
    void assign(Long userId, Long tenantId, List<UserRoleEntity> userRoles);
}
