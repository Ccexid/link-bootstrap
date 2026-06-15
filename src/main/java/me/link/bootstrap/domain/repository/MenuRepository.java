package me.link.bootstrap.domain.repository;

import me.link.bootstrap.domain.valueobject.StatusEnum;

import me.link.bootstrap.domain.entity.MenuEntity;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;

import java.util.List;
import java.util.Optional;

/**
 * 菜单仓储接口，定义领域层访问菜单持久化数据所需的抽象能力。
 */
public interface MenuRepository {

    MenuEntity save(MenuEntity menu);

    boolean update(MenuEntity menu);

    Optional<MenuEntity> findById(Long id);

    PageResult<MenuEntity> page(Integer pageNo, Integer pageSize, String name, String permission, Integer type, Long parentId, StatusEnum status, List<SortingField> sortingFields);

    boolean deleteById(Long id);
}
