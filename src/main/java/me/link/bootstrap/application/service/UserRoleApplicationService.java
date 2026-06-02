package me.link.bootstrap.application.service;

import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.command.AssignUserRoleCommand;
import me.link.bootstrap.application.command.CreateUserRoleCommand;
import me.link.bootstrap.application.command.UserRolePageQuery;
import me.link.bootstrap.application.command.UpdateUserRoleCommand;
import me.link.bootstrap.domain.entity.UserRoleEntity;
import me.link.bootstrap.domain.factory.UserRoleFactory;
import me.link.bootstrap.domain.repository.UserRoleRepository;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import me.link.bootstrap.shared.kernel.util.SecurityHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户角色关联应用服务，负责编排用户-角色关联的增删改查和批量分配流程。
 * <p>
 * 租户ID从当前登录用户的上下文中自动获取，确保数据隔离安全性。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class UserRoleApplicationService {

    private final UserRoleRepository userRoleRepository;

    /**
     * 创建用户角色关联。
     * <p>
     * 租户ID从当前登录用户的上下文中自动获取。
     * </p>
     */
    @Transactional
    public UserRoleEntity create(CreateUserRoleCommand command) {
        Long tenantId = SecurityHelper.getTenantId();
        UserRoleEntity userRole = UserRoleFactory.create(command.userId(), command.roleId(), tenantId);
        return userRoleRepository.save(userRole);
    }

    /**
     * 根据主键查询用户角色关联详情。
     */
    public UserRoleEntity get(Long id) {
        return userRoleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_ROLE_NOT_FOUND));
    }

    /**
     * 分页查询用户角色关联列表。
     * <p>
     * 租户ID从当前登录用户的上下文中自动获取。
     * </p>
     */
    public PageResult<UserRoleEntity> page(UserRolePageQuery query) {
        Long tenantId = SecurityHelper.getTenantId();
        return userRoleRepository.page(query.pageNo(), query.pageSize(), query.userId(), query.roleId(), tenantId, query.sortingFields());
    }

    /**
     * 更新用户角色关联信息。
     * <p>
     * 租户ID从当前登录用户的上下文中自动获取。
     * </p>
     */
    @Transactional
    public UserRoleEntity update(UpdateUserRoleCommand command) {
        UserRoleEntity userRole = get(command.id());
        Long tenantId = SecurityHelper.getTenantId();
        UserRoleFactory.changeBasicInfo(userRole, command.userId(), command.roleId(), tenantId);
        boolean updated = userRoleRepository.update(userRole);
        if (!updated) {
            throw new BusinessException(ErrorCode.USER_ROLE_NOT_FOUND);
        }
        return get(command.id());
    }

    /**
     * 批量分配用户角色（覆盖式）。
     * <p>
     * 租户ID从当前登录用户的上下文中自动获取。
     * 先删除该用户在该租户下的所有旧角色关联，再批量插入新的角色关联。
     * </p>
     */
    @Transactional
    public void assign(AssignUserRoleCommand command) {
        if (command.userId() == null || command.userId() <= 0) {
            throw new IllegalArgumentException("用户角色关联userId必须大于0");
        }
        Long tenantId = SecurityHelper.getTenantId();
        List<UserRoleEntity> userRoles = command.roleIds() == null ? List.of() : command.roleIds().stream()
                .distinct()
                .map(roleId -> UserRoleFactory.create(command.userId(), roleId, tenantId))
                .toList();
        userRoleRepository.assign(command.userId(), tenantId, userRoles);
    }

    /**
     * 删除用户角色关联。
     */
    @Transactional
    public void delete(Long id) {
        if (!userRoleRepository.deleteById(id)) {
            throw new BusinessException(ErrorCode.USER_ROLE_NOT_FOUND);
        }
    }
}
