package me.link.bootstrap.application.service.impl;

import me.link.bootstrap.application.service.RoleService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.infrastructure.persistence.mapper.RoleMapper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.support.ApplicationAssert;
import me.link.bootstrap.shared.kernel.valueobject.StatusEnum;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;
import me.link.bootstrap.application.service.RoleService;
import me.link.bootstrap.infrastructure.persistence.po.RolePO;
import me.link.bootstrap.interfaces.dto.response.vo.RoleResponseVO;
import me.link.bootstrap.infrastructure.persistence.support.PageOrderHelper;
import me.link.bootstrap.infrastructure.security.PermissionCacheService;
import me.link.bootstrap.interfaces.dto.request.role.RoleCreateRequest;
import me.link.bootstrap.interfaces.dto.request.role.RolePageRequest;
import me.link.bootstrap.interfaces.dto.request.role.RoleUpdateRequest;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import me.link.bootstrap.shared.kernel.util.SecurityHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

/**
 * 角色应用服务，负责编排角色创建、查询、更新和删除流程。
 * <p>
 * 角色模块采用轻量三层结构，直接使用 RolePO 和 MyBatis-Plus ServiceImpl；
 * 多租户隔离由 {@code TenantLineInnerInterceptor} 全局处理。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, RolePO> implements RoleService {

    private static final Map<String, String> SORT_FIELD_MAPPING = Map.of(
            "id", "id",
            "created_at", "create_time",
            "updated_at", "update_time",
            "name", "name",
            "code", "code",
            "sort", "sort",
            "tenant_id", "tenant_id"
    );
    private final PermissionCacheService permissionCacheService;

    /**
     * 创建角色。
     * <p>
     * 包含权限编码（code）的唯一性校验，确保同一租户下编码不重复。
     * </p>
     */
    @Transactional
    public RoleResponseVO create(RoleCreateRequest request) {
        Long tenantId = SecurityHelper.getRequiredTenantId();
        validateCodeUnique(tenantId, request.getCode(), null);

        RolePO role = new RolePO();
        fillRole(role, request.getName(), request.getCode(), request.getSort(), request.getDataScope(), request.getDataScopeDeptIds(), request.getStatus(), request.getType(), request.getRemark(), tenantId);
        save(role);
        return toResponse(role);
    }

    /**
     * 根据主键查询角色详情。
     */
    public RoleResponseVO get(Long id) {
        return toResponse(getRequired(id));
    }

    /**
     * 分页查询角色列表。
     */
    public PageResult<RoleResponseVO> page(RolePageRequest request) {
        Page<RolePO> page = Page.of(request.getPageNo(), request.getPageSize());
        PageOrderHelper.applyOrders(page, request.getSortingFields(), SORT_FIELD_MAPPING);
        LambdaQueryWrapper<RolePO> wrapper = new LambdaQueryWrapper<RolePO>()
                .like(StrUtil.isNotBlank(request.getName()), RolePO::getName, request.getName())
                .like(StrUtil.isNotBlank(request.getCode()), RolePO::getCode, request.getCode())
                .eq(request.getStatus() != null, RolePO::getStatus, request.getStatus())
                .eq(request.getType() != null, RolePO::getType, request.getType())
                .orderByDesc(request.getSortingFields() == null || request.getSortingFields().isEmpty(), RolePO::getId);
        Page<RolePO> result = page(page, wrapper);
        return new PageResult<>(result.getRecords().stream().map(this::toResponse).toList(), result.getTotal());
    }

    /**
     * 更新角色信息。
     * <p>
     * 包含权限编码（code）的唯一性校验，确保同一租户下编码不重复（排除自身）。
     * </p>
     */
    @Transactional
    public RoleResponseVO update(Long id, RoleUpdateRequest request) {
        RolePO role = getRequired(id);
        Long tenantId = SecurityHelper.getRequiredTenantId();
        validateCodeUnique(tenantId, request.getCode(), id);
        fillRole(role, request.getName(), request.getCode(), request.getSort(), request.getDataScope(), request.getDataScopeDeptIds(), request.getStatus(), request.getType(), request.getRemark(), tenantId);
        ApplicationAssert.requireSuccess(updateById(role), ErrorCode.ROLE_NOT_FOUND);
        // 角色信息(如 status、code)变化会影响所有持有该角色用户的权限码 / 角色码,级联失效缓存
        permissionCacheService.evictByRoleId(id);
        return get(id);
    }

    /**
     * 删除角色。
     */
    @Transactional
    public void delete(Long id) {
        // 必须在 delete 之前 evict,evict 内部查 user_role 来拿受影响 userIds
        permissionCacheService.evictByRoleId(id);
        ApplicationAssert.requireSuccess(removeById(id), ErrorCode.ROLE_NOT_FOUND);
    }

    /**
     * 校验角色权限编码在当前租户下的唯一性。
     * <p>
     * 结合Sa-Token上下文获取租户ID，确保同一租户下角色编码不重复。
     * 更新操作时排除自身记录（excludeId参数），允许保持原有编码不变。
     * </p>
     *
     * @param tenantId   当前登录用户的租户ID
     * @param code       待校验的角色权限编码
     * @param excludeId  排除的角色ID（更新时传入自身ID，创建时传null）
     * @throws BusinessException 如果编码已存在则抛出异常
     */
    private void validateCodeUnique(Long tenantId, String code, Long excludeId) {
        findByTenantIdAndCode(tenantId, code).ifPresent(existingRole -> {
            if (excludeId == null || !existingRole.getId().equals(excludeId)) {
                throw new BusinessException(ErrorCode.ROLE_CODE_DUPLICATE);
            }
        });
    }

    private Optional<RolePO> findByTenantIdAndCode(Long tenantId, String code) {
        LambdaQueryWrapper<RolePO> wrapper = new LambdaQueryWrapper<RolePO>()
                .eq(RolePO::getTenantId, tenantId)
                .eq(RolePO::getCode, code)
                .last("LIMIT 1");
        return Optional.ofNullable(getOne(wrapper));
    }

    private void fillRole(RolePO role, String name, String code, Integer sort, Integer dataScope, String dataScopeDeptIds,
                          StatusEnum status, Integer type, String remark, Long tenantId) {
        validateBasicFields(name, code);
        role.setName(name.trim());
        role.setCode(code.trim());
        role.setSort(sort);
        role.setDataScope(dataScope);
        role.setDataScopeDeptIds(dataScopeDeptIds);
        role.setStatus(status);
        role.setType(type);
        role.setRemark(remark);
        role.setTenantId(tenantId);
    }

    private void validateBasicFields(String name, String code) {
        if (StrUtil.isBlank(name)) {
            ApplicationAssert.invalidParam("角色name不能为空");
        }
        if (StrUtil.isBlank(code)) {
            ApplicationAssert.invalidParam("角色code不能为空");
        }
    }

    private RolePO getRequired(Long id) {
        return ApplicationAssert.requireFound(getById(id), ErrorCode.ROLE_NOT_FOUND);
    }

    private RoleResponseVO toResponse(RolePO source) {
        RoleResponseVO response = BeanUtil.copyProperties(source, RoleResponseVO.class);
        response.setCreatedAt(source.getCreateTime());
        response.setUpdatedAt(source.getUpdateTime());
        return response;
    }
}
