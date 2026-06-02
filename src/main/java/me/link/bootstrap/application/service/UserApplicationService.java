package me.link.bootstrap.application.service;

import cn.hutool.crypto.digest.BCrypt;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.command.CreateUserCommand;
import me.link.bootstrap.application.command.UserPageQuery;
import me.link.bootstrap.application.command.UpdateUserCommand;
import me.link.bootstrap.domain.entity.UserEntity;
import me.link.bootstrap.domain.factory.UserFactory;
import me.link.bootstrap.domain.repository.UserRepository;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import me.link.bootstrap.shared.kernel.util.SecurityHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户应用服务，负责编排用户创建、查询、更新和删除流程。
 * <p>
 * 租户ID从当前登录用户的上下文中自动获取，确保数据隔离安全性。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final UserRepository userRepository;

    /**
     * 创建用户。
     * <p>
     * 租户ID从当前登录用户的上下文中自动获取。
     * </p>
     */
    @Transactional
    public UserEntity create(CreateUserCommand command) {
        String encryptedPassword = BCrypt.hashpw(command.password(), BCrypt.gensalt());
        Long tenantId = SecurityHelper.getTenantId();
        UserEntity user = UserFactory.create(command.username(), encryptedPassword, command.nickname(), command.userType(), command.mobile(), command.avatar(), command.status(), command.orgId(), command.deptId(), command.loginIp(), command.loginDate(), tenantId);
        return userRepository.save(user);
    }

    /**
     * 根据主键查询用户详情。
     */
    public UserEntity get(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * 分页查询用户列表。
     * <p>
     * 租户ID从当前登录用户的上下文中自动获取。
     * </p>
     */
    public PageResult<UserEntity> page(UserPageQuery query) {
        Long tenantId = SecurityHelper.getTenantId();
        return userRepository.page(query.pageNo(), query.pageSize(), query.username(), query.nickname(), query.mobile(), query.userType(), query.status(), tenantId, query.sortingFields());
    }

    /**
     * 更新用户信息。
     * <p>
     * 租户ID从当前登录用户的上下文中自动获取。
     * </p>
     */
    @Transactional
    public UserEntity update(UpdateUserCommand command) {
        UserEntity user = get(command.id());
        Long tenantId = SecurityHelper.getTenantId();
        UserFactory.changeBasicInfo(user, command.username(), command.password(), command.nickname(), command.userType(), command.mobile(), command.avatar(), command.status(), command.orgId(), command.deptId(), command.loginIp(), command.loginDate(), tenantId);
        boolean updated = userRepository.update(user);
        if (!updated) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return get(command.id());
    }

    /**
     * 删除用户。
     */
    @Transactional
    public void delete(Long id) {
        if (!userRepository.deleteById(id)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
    }
}
