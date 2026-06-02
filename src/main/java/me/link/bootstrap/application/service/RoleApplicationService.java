package me.link.bootstrap.application.service;

import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.command.CreateRoleCommand;
import me.link.bootstrap.application.command.RolePageQuery;
import me.link.bootstrap.application.command.UpdateRoleCommand;
import me.link.bootstrap.domain.entity.RoleEntity;
import me.link.bootstrap.domain.factory.RoleFactory;
import me.link.bootstrap.domain.repository.RoleRepository;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import me.link.bootstrap.shared.kernel.util.SecurityHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 角色应用服务，负责编排角色创建、查询、更新和删除流程。
 * <p>
 * 租户ID从当前登录用户的上下文中自动获取，确保数据隔离安全性。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class RoleApplicationService {

    private final RoleRepository roleRepository;

    /**
     * 创建角色。
     * <p>
     * 租户ID从当前登录用户的上下文中自动获取。
     * </p>
     */
    @Transactional
    public RoleEntity create(CreateRoleCommand command) {
        Long tenantId = SecurityHelper.getTenantId();
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
     * <p>
     * 租户ID从当前登录用户的上下文中自动获取。
     * </p>
     */
    public PageResult<RoleEntity> page(RolePageQuery query) {
        Long tenantId = SecurityHelper.getTenantId();
        return roleRepository.page(query.pageNo(), query.pageSize(), query.name(), query.code(), query.status(), query.type(), tenantId, query.sortingFields());
    }

    /**
     * 更新角色信息。
     * <p>
     * 租户ID从当前登录用户的上下文中自动获取。
     * </p>
     */
    @Transactional
    public RoleEntity update(UpdateRoleCommand command) {
        RoleEntity role = get(command.id());
        Long tenantId = SecurityHelper.getTenantId();
        RoleFactory.changeBasicInfo(role, command.name(), command.code(), command.sort(), command.dataScope(), command.dataScopeDeptIds(), command.status(), command.type(), command.remark(), tenantId);
        boolean updated = roleRepository.update(role);
        if (!updated) {
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
        }
        return get(command.id());
    }

    /**
     * 删除角色。
     */
    @Transactional
    public void delete(Long id) {
        if (!roleRepository.deleteById(id)) {
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
        }
    }
}
