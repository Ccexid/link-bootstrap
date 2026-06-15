package me.link.bootstrap.infrastructure.persistence.repository;

import me.link.bootstrap.domain.valueobject.StatusEnum;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.domain.entity.RoleEntity;
import me.link.bootstrap.domain.repository.RoleRepository;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.infrastructure.persistence.converter.RoleConverter;
import me.link.bootstrap.infrastructure.persistence.internal.RoleInternalService;
import me.link.bootstrap.infrastructure.persistence.po.RolePO;
import me.link.bootstrap.infrastructure.persistence.repository.support.PageOrderHelper;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository {

    private static final Map<String, String> SORT_FIELD_MAPPING = Map.of(
            "id", "id",
            "created_at", "create_time",
            "updated_at", "update_time",
            "name", "name",
            "code", "code",
            "sort", "sort",
            "tenant_id", "tenant_id"
    );

    private final RoleInternalService roleInternalService;
    private final RoleConverter roleConverter;

    @Override
    public RoleEntity save(RoleEntity role) {
        RolePO rolePO = roleConverter.convert(role);
        roleInternalService.save(rolePO);
        return roleConverter.reverseConvert(rolePO);
    }

    @Override
    public boolean update(RoleEntity role) {
        RolePO rolePO = roleConverter.convert(role);
        return roleInternalService.updateById(rolePO);
    }

    @Override
    public Optional<RoleEntity> findById(Long id) {
        return Optional.ofNullable(roleInternalService.getById(id))
                .map(roleConverter::reverseConvert);
    }

    @Override
    public PageResult<RoleEntity> page(Integer pageNo, Integer pageSize, String name, String code, StatusEnum status, Integer type, Long tenantId, List<SortingField> sortingFields) {
        Page<RolePO> page = Page.of(pageNo, pageSize);
        PageOrderHelper.applyOrders(page, sortingFields, SORT_FIELD_MAPPING);
        LambdaQueryWrapper<RolePO> wrapper = new LambdaQueryWrapper<RolePO>()
                .like(StrUtil.isNotBlank(name), RolePO::getName, name)
                .like(StrUtil.isNotBlank(code), RolePO::getCode, code)
                .eq(status != null, RolePO::getStatus, status)
                .eq(type != null, RolePO::getType, type)
                .eq(tenantId != null, RolePO::getTenantId, tenantId)
                .orderByDesc(sortingFields == null || sortingFields.isEmpty(), RolePO::getId);
        Page<RolePO> result = roleInternalService.page(page, wrapper);
        return new PageResult<>(roleConverter.reverseConvertList(result.getRecords()), result.getTotal());
    }

    @Override
    public boolean deleteById(Long id) {
        return roleInternalService.removeById(id);
    }

    /**
     * 根据租户ID和权限编码查询角色。
     * <p>
     * 实现角色编码在租户范围内的唯一性查询，支持Sa-Token上下文的数据隔离。
     * </p>
     */
    @Override
    public Optional<RoleEntity> findByTenantIdAndCode(Long tenantId, String code) {
        LambdaQueryWrapper<RolePO> wrapper = new LambdaQueryWrapper<RolePO>()
                .eq(RolePO::getTenantId, tenantId)
                .eq(RolePO::getCode, code)
                .last("LIMIT 1");
        return Optional.ofNullable(roleInternalService.getOne(wrapper))
                .map(roleConverter::reverseConvert);
    }

}
