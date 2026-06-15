package me.link.bootstrap.application.service;

import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.support.ApplicationAssert;
import me.link.bootstrap.application.command.CreateUserCommand;
import me.link.bootstrap.application.command.UserPageQuery;
import me.link.bootstrap.application.command.UpdateUserCommand;
import me.link.bootstrap.domain.entity.UserEntity;
import me.link.bootstrap.domain.factory.UserFactory;
import me.link.bootstrap.domain.repository.UserRepository;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import me.link.bootstrap.shared.kernel.util.SecurityHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户应用服务，负责编排用户创建、查询、更新和删除流程。
 * <p>
 * 多租户隔离由 {@code TenantLineInnerInterceptor} 全局处理：所有针对 system_users 表
 * 的 SELECT/UPDATE/DELETE 自动追加 {@code tenant_id = ?} 条件，规避水平越权（IDOR）。
 * 如确需跨租户操作（如超管），在方法/类上标注
 * {@link me.link.bootstrap.shared.kernel.database.mybatis.TenantIgnore @TenantIgnore}
 * 显式放行。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final UserRepository userRepository;

    /**
     * 创建用户。
     */
    @Transactional
    public UserEntity create(CreateUserCommand command) {
        Long tenantId = SecurityHelper.getRequiredTenantId();
        UserEntity user = UserFactory.create(command.username(), command.password(), command.nickname(), command.userType(), command.mobile(), command.avatar(), command.status(), command.orgId(), command.deptId(), command.loginIp(), command.loginDate(), tenantId);
        return userRepository.save(user);
    }

    /**
     * 根据主键查询用户详情。
     */
    public UserEntity get(Long id) {
        return ApplicationAssert.requireFound(userRepository.findById(id), ErrorCode.USER_NOT_FOUND);
    }

    /**
     * 分页查询用户列表。
     */
    public PageResult<UserEntity> page(UserPageQuery query) {
        return userRepository.page(query.pageNo(), query.pageSize(), query.username(), query.nickname(), query.mobile(), query.userType(), query.status(), null, query.sortingFields());
    }

    /**
     * 更新用户信息。
     */
    @Transactional
    public UserEntity update(UpdateUserCommand command) {
        UserEntity user = get(command.id());
        Long tenantId = SecurityHelper.getRequiredTenantId();
        UserFactory.changeBasicInfo(user, command.username(), command.password(), command.nickname(), command.userType(), command.mobile(), command.avatar(), command.status(), command.orgId(), command.deptId(), command.loginIp(), command.loginDate(), tenantId);
        ApplicationAssert.requireSuccess(userRepository.update(user), ErrorCode.USER_NOT_FOUND);
        return get(command.id());
    }

    /**
     * 删除用户。
     */
    @Transactional
    public void delete(Long id) {
        ApplicationAssert.requireSuccess(userRepository.deleteById(id), ErrorCode.USER_NOT_FOUND);
    }
}
