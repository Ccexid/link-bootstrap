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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleApplicationService {

    private final RoleRepository roleRepository;

    @Transactional
    public RoleEntity create(CreateRoleCommand command) {
        RoleEntity role = RoleFactory.create(command.name(), command.code(), command.sort(), command.dataScope(), command.dataScopeDeptIds(), command.status(), command.type(), command.remark(), command.tenantId());
        return roleRepository.save(role);
    }

    public RoleEntity get(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));
    }

    public PageResult<RoleEntity> page(RolePageQuery query) {
        return roleRepository.page(query.pageNo(), query.pageSize(), query.name(), query.code(), query.status(), query.type(), query.tenantId(), query.sortingFields());
    }

    @Transactional
    public RoleEntity update(UpdateRoleCommand command) {
        RoleEntity role = get(command.id());
        RoleFactory.changeBasicInfo(role, command.name(), command.code(), command.sort(), command.dataScope(), command.dataScopeDeptIds(), command.status(), command.type(), command.remark(), command.tenantId());
        boolean updated = roleRepository.update(role);
        if (!updated) {
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
        }
        return get(command.id());
    }

    @Transactional
    public void delete(Long id) {
        if (!roleRepository.deleteById(id)) {
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
        }
    }
}
