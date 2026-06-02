package me.link.bootstrap.infrastructure.persistence.repository;

import me.link.bootstrap.domain.valueobject.StatusEnum;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.domain.entity.MenuEntity;
import me.link.bootstrap.domain.repository.MenuRepository;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.infrastructure.persistence.converter.MenuConverter;
import me.link.bootstrap.infrastructure.persistence.internal.MenuInternalService;
import me.link.bootstrap.infrastructure.persistence.po.MenuPO;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MenuRepositoryImpl implements MenuRepository {

    private static final Map<String, String> SORT_FIELD_MAPPING = Map.of(
            "id", "id",
            "created_at", "create_time",
            "updated_at", "update_time",
            "name", "name",
            "sort", "sort",
            "parent_id", "parent_id"
    );

    private final MenuInternalService menuInternalService;
    private final MenuConverter menuConverter;

    @Override
    public MenuEntity save(MenuEntity menu) {
        MenuPO menuPO = menuConverter.convert(menu);
        menuInternalService.save(menuPO);
        return menuConverter.reverseConvert(menuPO);
    }

    @Override
    public boolean update(MenuEntity menu) {
        MenuPO menuPO = menuConverter.convert(menu);
        return menuInternalService.updateById(menuPO);
    }

    @Override
    public Optional<MenuEntity> findById(Long id) {
        return Optional.ofNullable(menuInternalService.getById(id))
                .map(menuConverter::reverseConvert);
    }

    @Override
    public PageResult<MenuEntity> page(Integer pageNo, Integer pageSize, String name, String permission, Integer type, Long parentId, StatusEnum status, List<SortingField> sortingFields) {
        Page<MenuPO> page = Page.of(pageNo, pageSize);
        applyOrders(page, sortingFields);
        LambdaQueryWrapper<MenuPO> wrapper = new LambdaQueryWrapper<MenuPO>()
                .like(StrUtil.isNotBlank(name), MenuPO::getName, name)
                .like(StrUtil.isNotBlank(permission), MenuPO::getPermission, permission)
                .eq(type != null, MenuPO::getType, type)
                .eq(parentId != null, MenuPO::getParentId, parentId)
                .eq(status != null, MenuPO::getStatus, status)
                .orderByDesc(sortingFields == null || sortingFields.isEmpty(), MenuPO::getId);
        Page<MenuPO> result = menuInternalService.page(page, wrapper);
        return new PageResult<>(menuConverter.reverseConvertList(result.getRecords()), result.getTotal());
    }

    @Override
    public boolean deleteById(Long id) {
        return menuInternalService.removeById(id);
    }

    private void applyOrders(Page<MenuPO> page, List<SortingField> sortingFields) {
        if (sortingFields == null || sortingFields.isEmpty()) {
            return;
        }
        sortingFields.stream()
                .map(this::toOrderItem)
                .forEach(page::addOrder);
    }

    private OrderItem toOrderItem(SortingField sortingField) {
        String column = SORT_FIELD_MAPPING.get(sortingField.getField());
        if (sortingField.isAsc()) {
            return OrderItem.asc(column);
        }
        return OrderItem.desc(column);
    }
}
