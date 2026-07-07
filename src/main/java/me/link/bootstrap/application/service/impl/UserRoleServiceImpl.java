package me.link.bootstrap.application.service.impl;

import cn.hutool.core.bean.BeanUtil;
import me.link.bootstrap.application.service.UserRoleService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.infrastructure.persistence.mapper.UserRoleMapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.support.ApplicationAssert;
import me.link.bootstrap.application.service.RoleService;
import me.link.bootstrap.application.service.UserService;
import me.link.bootstrap.application.service.UserRoleService;
import me.link.bootstrap.infrastructure.persistence.po.RolePO;
import me.link.bootstrap.infrastructure.persistence.po.UserPO;
import me.link.bootstrap.infrastructure.persistence.po.UserRolePO;
import me.link.bootstrap.interfaces.dto.response.vo.UserRoleResponseVO;
import me.link.bootstrap.infrastructure.persistence.support.PageOrderHelper;
import me.link.bootstrap.infrastructure.security.PermissionCacheService;
import me.link.bootstrap.interfaces.dto.request.userrole.UserRoleAssignRequest;
import me.link.bootstrap.interfaces.dto.request.userrole.UserRoleCreateRequest;
import me.link.bootstrap.interfaces.dto.request.userrole.UserRolePageRequest;
import me.link.bootstrap.interfaces.dto.request.userrole.UserRoleUpdateRequest;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import me.link.bootstrap.shared.kernel.util.SecurityHelper;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 用户角色关联服务，直接编排用户-角色关联增删改查、覆盖式分配和权限缓存失效。
 * <p>
 * 多租户隔离由 {@code TenantLineInnerInterceptor} 全局处理：所有针对用户角色表
 * 的 SELECT/UPDATE/DELETE 自动追加 {@code tenant_id = ?} 条件，规避水平越权（IDOR）。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRolePO> implements UserRoleService {

    private static final Map<String, String> SORT_FIELD_MAPPING = Map.of(
            "id", "id",
            "created_at", "create_time",
            "updated_at", "update_time",
            "user_id", "user_id",
            "role_id", "role_id",
            "tenant_id", "tenant_id"
    );
    private final UserService userService;
    private final RoleService roleService;
    private final PermissionCacheService permissionCacheService;

    @Transactional
    public UserRoleResponseVO create(UserRoleCreateRequest request) {
        Long tenantId = SecurityHelper.getRequiredTenantId();
        validateReferences(request.getUserId(), request.getRoleId(), tenantId);
        validateUniqueBinding(request.getUserId(), request.getRoleId(), tenantId, null);
        UserRolePO userRole = createPO(request.getUserId(), request.getRoleId(), tenantId);
        save(userRole);
        permissionCacheService.evictByUserId(request.getUserId());
        return toResponse(userRole);
    }

    public UserRoleResponseVO get(Long id) {
        return toResponse(getRequired(id));
    }

    public PageResult<UserRoleResponseVO> page(UserRolePageRequest request) {
        Page<UserRolePO> page = Page.of(request.getPageNo(), request.getPageSize());
        PageOrderHelper.applyOrders(page, request.getSortingFields(), SORT_FIELD_MAPPING);
        LambdaQueryWrapper<UserRolePO> wrapper = new LambdaQueryWrapper<UserRolePO>()
                .eq(request.getUserId() != null, UserRolePO::getUserId, request.getUserId())
                .eq(request.getRoleId() != null, UserRolePO::getRoleId, request.getRoleId())
                .orderByDesc(request.getSortingFields() == null || request.getSortingFields().isEmpty(), UserRolePO::getId);
        Page<UserRolePO> result = page(page, wrapper);
        return new PageResult<>(result.getRecords().stream().map(this::toResponse).toList(), result.getTotal());
    }

    @Transactional
    public UserRoleResponseVO update(Long id, UserRoleUpdateRequest request) {
        UserRolePO userRole = getRequired(id);
        Long tenantId = SecurityHelper.getRequiredTenantId();
        Long oldUserId = userRole.getUserId();
        validateReferences(request.getUserId(), request.getRoleId(), tenantId);
        validateUniqueBinding(request.getUserId(), request.getRoleId(), tenantId, id);
        applyMutableFields(userRole, request.getUserId(), request.getRoleId(), tenantId);
        ApplicationAssert.requireSuccess(updateById(userRole), ErrorCode.USER_ROLE_NOT_FOUND);
        permissionCacheService.evictByUserId(oldUserId);
        if (!oldUserId.equals(request.getUserId())) {
            permissionCacheService.evictByUserId(request.getUserId());
        }
        return get(id);
    }

    /**
     * 批量分配用户角色（覆盖式）。
     * <p>
     * 先删除该用户在该租户下的所有旧角色关联，再批量插入新的角色关联。
     * </p>
     */
    @Transactional
    public void assign(UserRoleAssignRequest request) {
        if (request.getUserId() == null || request.getUserId() <= 0) {
            ApplicationAssert.invalidParam("用户角色关联userId必须大于0");
        }
        Long tenantId = SecurityHelper.getRequiredTenantId();
        List<Long> roleIds = request.getRoleIds() == null ? List.of() : request.getRoleIds().stream()
                .distinct()
                .toList();
        validateUserInCurrentTenant(request.getUserId(), tenantId);
        validateRoleIdsInCurrentTenant(roleIds, tenantId);
        List<UserRolePO> userRoles = roleIds.stream()
                .map(roleId -> createPO(request.getUserId(), roleId, tenantId))
                .toList();
        remove(new LambdaQueryWrapper<UserRolePO>()
                .eq(UserRolePO::getUserId, request.getUserId())
                .eq(UserRolePO::getTenantId, tenantId));
        if (!userRoles.isEmpty()) {
            saveBatch(userRoles);
        }
        permissionCacheService.evictByUserId(request.getUserId());
    }

    @Transactional
    public void delete(Long id) {
        // 先 get 拿 userId 用于 evict,再删除
        UserRolePO userRole = getRequired(id);
        ApplicationAssert.requireSuccess(removeById(id), ErrorCode.USER_ROLE_NOT_FOUND);
        permissionCacheService.evictByUserId(userRole.getUserId());
    }

    private static UserRolePO createPO(Long userId, Long roleId, Long tenantId) {
        UserRolePO userRole = new UserRolePO();
        applyMutableFields(userRole, userId, roleId, tenantId);
        return userRole;
    }

    private void validateReferences(Long userId, Long roleId, Long tenantId) {
        if (userId == null || userId <= 0) {
            ApplicationAssert.invalidParam("用户角色关联userId必须大于0");
        }
        if (roleId == null || roleId <= 0) {
            ApplicationAssert.invalidParam("用户角色关联roleId必须大于0");
        }
        validateUserInCurrentTenant(userId, tenantId);
        validateRoleInCurrentTenant(roleId, tenantId);
    }

    private void validateUserInCurrentTenant(Long userId, Long tenantId) {
        UserPO user = userService.getById(userId);
        if (user == null || !Objects.equals(user.getTenantId(), tenantId)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
    }

    private void validateRoleInCurrentTenant(Long roleId, Long tenantId) {
        RolePO role = roleService.getById(roleId);
        if (role == null || !Objects.equals(role.getTenantId(), tenantId)) {
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
        }
    }

    private void validateRoleIdsInCurrentTenant(List<Long> roleIds, Long tenantId) {
        for (Long roleId : roleIds) {
            if (roleId == null || roleId <= 0) {
                ApplicationAssert.invalidParam("用户角色关联roleId必须大于0");
            }
            validateRoleInCurrentTenant(roleId, tenantId);
        }
    }

    private void validateUniqueBinding(Long userId, Long roleId, Long tenantId, Long excludeId) {
        boolean exists = exists(new LambdaQueryWrapper<UserRolePO>()
                .eq(UserRolePO::getUserId, userId)
                .eq(UserRolePO::getRoleId, roleId)
                .eq(UserRolePO::getTenantId, tenantId)
                .ne(excludeId != null, UserRolePO::getId, excludeId));
        if (exists) {
            ApplicationAssert.invalidParam("用户角色关联已存在");
        }
    }

    private static void applyMutableFields(UserRolePO userRole, Long userId, Long roleId, Long tenantId) {
        if (userId == null || userId <= 0) {
            ApplicationAssert.invalidParam("用户角色关联userId必须大于0");
        }
        if (roleId == null || roleId <= 0) {
            ApplicationAssert.invalidParam("用户角色关联roleId必须大于0");
        }
        if (tenantId == null || tenantId <= 0) {
            ApplicationAssert.invalidParam("用户角色关联tenantId必须大于0");
        }
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        userRole.setTenantId(tenantId);
    }

    private UserRolePO getRequired(Long id) {
        return ApplicationAssert.requireFound(getById(id), ErrorCode.USER_ROLE_NOT_FOUND);
    }

    private UserRoleResponseVO toResponse(UserRolePO source) {
        UserRoleResponseVO response = BeanUtil.copyProperties(source, UserRoleResponseVO.class);
        response.setCreatedAt(source.getCreateTime());
        response.setUpdatedAt(source.getUpdateTime());
        return response;
    }
}
