package me.link.bootstrap.application.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final UserRepository userRepository;

    @Transactional
    public UserEntity create(CreateUserCommand command) {
        UserEntity user = UserFactory.create(command.username(), command.password(), command.nickname(), command.userType(), command.mobile(), command.avatar(), command.status(), command.orgId(), command.deptId(), command.loginIp(), command.loginDate(), command.tenantId());
        return userRepository.save(user);
    }

    public UserEntity get(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    public PageResult<UserEntity> page(UserPageQuery query) {
        return userRepository.page(query.pageNo(), query.pageSize(), query.username(), query.nickname(), query.mobile(), query.userType(), query.status(), query.tenantId(), query.sortingFields());
    }

    @Transactional
    public UserEntity update(UpdateUserCommand command) {
        UserEntity user = get(command.id());
        UserFactory.changeBasicInfo(user, command.username(), command.password(), command.nickname(), command.userType(), command.mobile(), command.avatar(), command.status(), command.orgId(), command.deptId(), command.loginIp(), command.loginDate(), command.tenantId());
        boolean updated = userRepository.update(user);
        if (!updated) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return get(command.id());
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepository.deleteById(id)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
    }
}
