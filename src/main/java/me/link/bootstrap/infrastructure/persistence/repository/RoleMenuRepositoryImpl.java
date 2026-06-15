package me.link.bootstrap.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.domain.entity.RoleMenuEntity;
import me.link.bootstrap.domain.repository.RoleMenuRepository;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.infrastructure.persistence.converter.RoleMenuConverter;
import me.link.bootstrap.infrastructure.persistence.internal.RoleMenuInternalService;
import me.link.bootstrap.infrastructure.persistence.po.RoleMenuPO;
import me.link.bootstrap.infrastructure.persistence.repository.support.PageOrderHelper;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RoleMenuRepositoryImpl implements RoleMenuRepository {

    private static final Map<String, String> SORT_FIELD_MAPPING = Map.of(
            "id", "id",
            "created_at", "create_time",
            "updated_at", "update_time",
            "role_id", "role_id",
            "menu_id", "menu_id",
            "tenant_id", "tenant_id"
    );

    private final RoleMenuInternalService roleMenuInternalService;
    private final RoleMenuConverter roleMenuConverter;

    @Override
    public RoleMenuEntity save(RoleMenuEntity roleMenu) {
        RoleMenuPO roleMenuPO = roleMenuConverter.convert(roleMenu);
        roleMenuInternalService.save(roleMenuPO);
        return roleMenuConverter.reverseConvert(roleMenuPO);
    }

    @Override
    public boolean update(RoleMenuEntity roleMenu) {
        RoleMenuPO roleMenuPO = roleMenuConverter.convert(roleMenu);
        return roleMenuInternalService.updateById(roleMenuPO);
    }

    @Override
    public Optional<RoleMenuEntity> findById(Long id) {
        return Optional.ofNullable(roleMenuInternalService.getById(id))
                .map(roleMenuConverter::reverseConvert);
    }

    @Override
    public PageResult<RoleMenuEntity> page(Integer pageNo, Integer pageSize, Long roleId, Long menuId, Long tenantId, List<SortingField> sortingFields) {
        Page<RoleMenuPO> page = Page.of(pageNo, pageSize);
        PageOrderHelper.applyOrders(page, sortingFields, SORT_FIELD_MAPPING);
        LambdaQueryWrapper<RoleMenuPO> wrapper = new LambdaQueryWrapper<RoleMenuPO>()
                .eq(roleId != null, RoleMenuPO::getRoleId, roleId)
                .eq(menuId != null, RoleMenuPO::getMenuId, menuId)
                .eq(tenantId != null, RoleMenuPO::getTenantId, tenantId)
                .orderByDesc(sortingFields == null || sortingFields.isEmpty(), RoleMenuPO::getId);
        Page<RoleMenuPO> result = roleMenuInternalService.page(page, wrapper);
        return new PageResult<>(roleMenuConverter.reverseConvertList(result.getRecords()), result.getTotal());
    }

    @Override
    public boolean deleteById(Long id) {
        return roleMenuInternalService.removeById(id);
    }

    @Override
    public void authorize(Long roleId, Long tenantId, List<RoleMenuEntity> roleMenus) {
        roleMenuInternalService.remove(new LambdaQueryWrapper<RoleMenuPO>()
                .eq(RoleMenuPO::getRoleId, roleId)
                .eq(tenantId != null, RoleMenuPO::getTenantId, tenantId));
        if (roleMenus == null || roleMenus.isEmpty()) {
            return;
        }
        roleMenuInternalService.saveBatch(roleMenuConverter.convertList(roleMenus));
    }

}
