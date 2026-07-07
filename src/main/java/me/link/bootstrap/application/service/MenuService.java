package me.link.bootstrap.application.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.infrastructure.persistence.po.MenuPO;
import me.link.bootstrap.infrastructure.persistence.po.MenuPO;
import me.link.bootstrap.interfaces.dto.request.menu.MenuCreateRequest;
import me.link.bootstrap.interfaces.dto.request.menu.MenuPageRequest;
import me.link.bootstrap.interfaces.dto.request.menu.MenuUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.vo.MenuResponseVO;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;

public interface MenuService extends IService<MenuPO> {

    MenuResponseVO create(MenuCreateRequest request);
    MenuResponseVO get(Long id);
    PageResult<MenuResponseVO> page(MenuPageRequest request);
    MenuResponseVO update(Long id, MenuUpdateRequest request);
    void delete(Long id);
}
