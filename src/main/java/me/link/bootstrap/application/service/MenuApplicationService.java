package me.link.bootstrap.application.service;

import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.command.CreateMenuCommand;
import me.link.bootstrap.application.command.MenuPageQuery;
import me.link.bootstrap.application.command.UpdateMenuCommand;
import me.link.bootstrap.domain.entity.MenuEntity;
import me.link.bootstrap.domain.factory.MenuFactory;
import me.link.bootstrap.domain.repository.MenuRepository;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MenuApplicationService {

    private final MenuRepository menuRepository;

    @Transactional
    public MenuEntity create(CreateMenuCommand command) {
        MenuEntity menu = MenuFactory.create(command.name(), command.permission(), command.type(), command.sort(), command.parentId(), command.path(), command.icon(), command.component(), command.componentName(), command.status(), command.visible(), command.keepAlive(), command.alwaysShow());
        return menuRepository.save(menu);
    }

    public MenuEntity get(Long id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));
    }

    public PageResult<MenuEntity> page(MenuPageQuery query) {
        return menuRepository.page(query.pageNo(), query.pageSize(), query.name(), query.permission(), query.type(), query.parentId(), query.status(), query.sortingFields());
    }

    @Transactional
    public MenuEntity update(UpdateMenuCommand command) {
        MenuEntity menu = get(command.id());
        MenuFactory.changeBasicInfo(menu, command.name(), command.permission(), command.type(), command.sort(), command.parentId(), command.path(), command.icon(), command.component(), command.componentName(), command.status(), command.visible(), command.keepAlive(), command.alwaysShow());
        boolean updated = menuRepository.update(menu);
        if (!updated) {
            throw new BusinessException(ErrorCode.MENU_NOT_FOUND);
        }
        return get(command.id());
    }

    @Transactional
    public void delete(Long id) {
        if (!menuRepository.deleteById(id)) {
            throw new BusinessException(ErrorCode.MENU_NOT_FOUND);
        }
    }
}
