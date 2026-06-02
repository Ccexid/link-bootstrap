package me.link.bootstrap.application.service;

import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.command.CreateUserRoleCommand;
import me.link.bootstrap.application.command.UserRolePageQuery;
import me.link.bootstrap.application.command.UpdateUserRoleCommand;
import me.link.bootstrap.domain.entity.UserRoleEntity;
import me.link.bootstrap.domain.factory.UserRoleFactory;
import me.link.bootstrap.domain.repository.UserRoleRepository;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserRoleApplicationService {

    private final UserRoleRepository userRoleRepository;

    @Transactional
    public UserRoleEntity create(CreateUserRoleCommand command) {
        UserRoleEntity userRole = UserRoleFactory.create(command.userId(), command.roleId(), command.tenantId());
        return userRoleRepository.save(userRole);
    }

    public UserRoleEntity get(Long id) {
        return userRoleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_ROLE_NOT_FOUND));
    }

    public PageResult<UserRoleEntity> page(UserRolePageQuery query) {
        return userRoleRepository.page(query.pageNo(), query.pageSize(), query.userId(), query.roleId(), query.tenantId(), query.sortingFields());
    }

    @Transactional
    public UserRoleEntity update(UpdateUserRoleCommand command) {
        UserRoleEntity userRole = get(command.id());
        UserRoleFactory.changeBasicInfo(userRole, command.userId(), command.roleId(), command.tenantId());
        boolean updated = userRoleRepository.update(userRole);
        if (!updated) {
            throw new BusinessException(ErrorCode.USER_ROLE_NOT_FOUND);
        }
        return get(command.id());
    }

    @Transactional
    public void delete(Long id) {
        if (!userRoleRepository.deleteById(id)) {
            throw new BusinessException(ErrorCode.USER_ROLE_NOT_FOUND);
        }
    }
}
