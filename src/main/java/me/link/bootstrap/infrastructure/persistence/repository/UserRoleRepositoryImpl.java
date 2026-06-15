package me.link.bootstrap.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.domain.entity.UserRoleEntity;
import me.link.bootstrap.domain.repository.UserRoleRepository;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.infrastructure.persistence.converter.UserRoleConverter;
import me.link.bootstrap.infrastructure.persistence.internal.UserRoleInternalService;
import me.link.bootstrap.infrastructure.persistence.po.UserRolePO;
import me.link.bootstrap.infrastructure.persistence.repository.support.PageOrderHelper;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRoleRepositoryImpl implements UserRoleRepository {

    private static final Map<String, String> SORT_FIELD_MAPPING = Map.of(
            "id", "id",
            "created_at", "create_time",
            "updated_at", "update_time",
            "user_id", "user_id",
            "role_id", "role_id",
            "tenant_id", "tenant_id"
    );

    private final UserRoleInternalService userRoleInternalService;
    private final UserRoleConverter userRoleConverter;

    @Override
    public UserRoleEntity save(UserRoleEntity userRole) {
        UserRolePO userRolePO = userRoleConverter.convert(userRole);
        userRoleInternalService.save(userRolePO);
        return userRoleConverter.reverseConvert(userRolePO);
    }

    @Override
    public boolean update(UserRoleEntity userRole) {
        UserRolePO userRolePO = userRoleConverter.convert(userRole);
        return userRoleInternalService.updateById(userRolePO);
    }

    @Override
    public Optional<UserRoleEntity> findById(Long id) {
        return Optional.ofNullable(userRoleInternalService.getById(id))
                .map(userRoleConverter::reverseConvert);
    }

    @Override
    public PageResult<UserRoleEntity> page(Integer pageNo, Integer pageSize, Long userId, Long roleId, Long tenantId, List<SortingField> sortingFields) {
        Page<UserRolePO> page = Page.of(pageNo, pageSize);
        PageOrderHelper.applyOrders(page, sortingFields, SORT_FIELD_MAPPING);
        LambdaQueryWrapper<UserRolePO> wrapper = new LambdaQueryWrapper<UserRolePO>()
                .eq(userId != null, UserRolePO::getUserId, userId)
                .eq(roleId != null, UserRolePO::getRoleId, roleId)
                .eq(tenantId != null, UserRolePO::getTenantId, tenantId)
                .orderByDesc(sortingFields == null || sortingFields.isEmpty(), UserRolePO::getId);
        Page<UserRolePO> result = userRoleInternalService.page(page, wrapper);
        return new PageResult<>(userRoleConverter.reverseConvertList(result.getRecords()), result.getTotal());
    }

    @Override
    public boolean deleteById(Long id) {
        return userRoleInternalService.removeById(id);
    }

    @Override
    public void assign(Long userId, Long tenantId, List<UserRoleEntity> userRoles) {
        userRoleInternalService.remove(new LambdaQueryWrapper<UserRolePO>()
                .eq(UserRolePO::getUserId, userId)
                .eq(tenantId != null, UserRolePO::getTenantId, tenantId));
        if (userRoles == null || userRoles.isEmpty()) {
            return;
        }
        userRoleInternalService.saveBatch(userRoleConverter.convertList(userRoles));
    }

}
