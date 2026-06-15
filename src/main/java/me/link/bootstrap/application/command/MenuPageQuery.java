package me.link.bootstrap.application.command;

import me.link.bootstrap.domain.valueobject.StatusEnum;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;

import java.util.List;

/**
 * 菜单分页查询对象，封装菜单列表筛选、分页和排序条件。
 */
public record MenuPageQuery(Integer pageNo, Integer pageSize, String name, String permission, Integer type, Long parentId, StatusEnum status, List<SortingField> sortingFields) {
}
