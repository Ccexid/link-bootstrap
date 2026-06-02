package me.link.bootstrap.application.service;

import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.command.AuthorizeRoleMenuCommand;
import me.link.bootstrap.application.command.CreateRoleMenuCommand;
import me.link.bootstrap.application.command.RoleMenuPageQuery;
import me.link.bootstrap.application.command.UpdateRoleMenuCommand;
import me.link.bootstrap.domain.entity.RoleMenuEntity;
import me.link.bootstrap.domain.factory.RoleMenuFactory;
import me.link.bootstrap.domain.repository.RoleMenuRepository;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleMenuApplicationService {

    private final RoleMenuRepository roleMenuRepository;

    @Transactional
    public RoleMenuEntity create(CreateRoleMenuCommand command) {
        RoleMenuEntity roleMenu = RoleMenuFactory.create(command.roleId(), command.menuId(), command.tenantId());
        return roleMenuRepository.save(roleMenu);
    }

    public RoleMenuEntity get(Long id) {
        return roleMenuRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_MENU_NOT_FOUND));
    }

    public PageResult<RoleMenuEntity> page(RoleMenuPageQuery query) {
        return roleMenuRepository.page(query.pageNo(), query.pageSize(), query.roleId(), query.menuId(), query.tenantId(), query.sortingFields());
    }

    @Transactional
    public RoleMenuEntity update(UpdateRoleMenuCommand command) {
        RoleMenuEntity roleMenu = get(command.id());
        RoleMenuFactory.changeBasicInfo(roleMenu, command.roleId(), command.menuId(), command.tenantId());
        boolean updated = roleMenuRepository.update(roleMenu);
        if (!updated) {
            throw new BusinessException(ErrorCode.ROLE_MENU_NOT_FOUND);
        }
        return get(command.id());
    }

    @Transactional
    public void authorize(AuthorizeRoleMenuCommand command) {
        if (command.roleId() == null || command.roleId() <= 0) {
            throw new IllegalArgumentException("角色菜单关联roleId必须大于0");
        }
        List<RoleMenuEntity> roleMenus = command.menuIds() == null ? List.of() : command.menuIds().stream()
                .distinct()
                .map(menuId -> RoleMenuFactory.create(command.roleId(), menuId, command.tenantId()))
                .toList();
        roleMenuRepository.authorize(command.roleId(), command.tenantId(), roleMenus);
    }

    @Transactional
    public void delete(Long id) {
        if (!roleMenuRepository.deleteById(id)) {
            throw new BusinessException(ErrorCode.ROLE_MENU_NOT_FOUND);
        }
    }
}
