package me.link.bootstrap.domain.repository;

import me.link.bootstrap.domain.entity.RoleMenuEntity;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;

import java.util.List;
import java.util.Optional;

/**
 * 角色菜单关联仓储接口，定义领域层访问角色授权关系所需的抽象能力。
 */
public interface RoleMenuRepository {

    RoleMenuEntity save(RoleMenuEntity roleMenu);

    boolean update(RoleMenuEntity roleMenu);

    Optional<RoleMenuEntity> findById(Long id);

    PageResult<RoleMenuEntity> page(Integer pageNo, Integer pageSize, Long roleId, Long menuId, Long tenantId, List<SortingField> sortingFields);

    boolean deleteById(Long id);

    /**
     * 覆盖式保存指定角色在当前租户下的菜单授权关系。
     */
    void authorize(Long roleId, Long tenantId, List<RoleMenuEntity> roleMenus);
}
