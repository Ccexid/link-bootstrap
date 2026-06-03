package me.link.bootstrap.application.service;

import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.command.CreateRoleCommand;
import me.link.bootstrap.application.command.RolePageQuery;
import me.link.bootstrap.application.command.UpdateRoleCommand;
import me.link.bootstrap.domain.entity.RoleEntity;
import me.link.bootstrap.domain.factory.RoleFactory;
import me.link.bootstrap.domain.repository.RoleRepository;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.infrastructure.security.PermissionCacheService;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import me.link.bootstrap.shared.kernel.util.SecurityHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 角色应用服务，负责编排角色创建、查询、更新和删除流程。
 * <p>
 * 多租户隔离由 {@code TenantLineInnerInterceptor} 全局处理：所有针对 system_role 表
 * 的 SELECT/UPDATE/DELETE 自动追加 {@code tenant_id = ?} 条件，规避水平越权（IDOR）。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class RoleApplicationService {

    private final RoleRepository roleRepository;
    private final PermissionCacheService permissionCacheService;

    /**
     * 创建角色。
     * <p>
     * 包含权限编码（code）的唯一性校验，确保同一租户下编码不重复。
     * </p>
     */
    @Transactional
    public RoleEntity create(CreateRoleCommand command) {
        Long tenantId = SecurityHelper.getRequiredTenantId();
        validateCodeUnique(tenantId, command.code(), null);
        RoleEntity role = RoleFactory.create(command.name(), command.code(), command.sort(), command.dataScope(), command.dataScopeDeptIds(), command.status(), command.type(), command.remark(), tenantId);
        return roleRepository.save(role);
    }

    /**
     * 根据主键查询角色详情。
     */
    public RoleEntity get(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));
    }

    /**
     * 分页查询角色列表。
     */
    public PageResult<RoleEntity> page(RolePageQuery query) {
        return roleRepository.page(query.pageNo(), query.pageSize(), query.name(), query.code(), query.status(), query.type(), null, query.sortingFields());
    }

    /**
     * 更新角色信息。
     * <p>
     * 包含权限编码（code）的唯一性校验，确保同一租户下编码不重复（排除自身）。
     * </p>
     */
    @Transactional
    public RoleEntity update(UpdateRoleCommand command) {
        RoleEntity role = get(command.id());
        Long tenantId = SecurityHelper.getRequiredTenantId();
        validateCodeUnique(tenantId, command.code(), command.id());
        RoleFactory.changeBasicInfo(role, command.name(), command.code(), command.sort(), command.dataScope(), command.dataScopeDeptIds(), command.status(), command.type(), command.remark(), tenantId);
        if (!roleRepository.update(role)) {
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
        }
        // 角色信息(如 status、code)变化会影响所有持有该角色用户的权限码 / 角色码,级联失效缓存
        permissionCacheService.evictByRoleId(command.id());
        return get(command.id());
    }

    /**
     * 删除角色。
     */
    @Transactional
    public void delete(Long id) {
        // 必须在 delete 之前 evict,evict 内部查 user_role 来拿受影响 userIds
        permissionCacheService.evictByRoleId(id);
        if (!roleRepository.deleteById(id)) {
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
        }
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
        roleRepository.findByTenantIdAndCode(tenantId, code).ifPresent(existingRole -> {
            if (excludeId == null || !existingRole.getId().equals(excludeId)) {
                throw new BusinessException(ErrorCode.ROLE_CODE_DUPLICATE);
            }
        });
    }
}
