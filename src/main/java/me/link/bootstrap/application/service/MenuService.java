package me.link.bootstrap.application.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.infrastructure.persistence.po.MenuPO;
import me.link.bootstrap.interfaces.dto.request.menu.MenuCreateRequest;
import me.link.bootstrap.interfaces.dto.request.menu.MenuPageRequest;
import me.link.bootstrap.interfaces.dto.request.menu.MenuUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.vo.MenuResponseVO;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;

public interface MenuService extends IService<MenuPO> {

    /**
     * 创建菜单。
     */
    MenuResponseVO create(MenuCreateRequest request);
    /**
     * 查询菜单详情。
     */
    MenuResponseVO get(Long id);
    /**
     * 分页查询菜单列表。
     */
    PageResult<MenuResponseVO> page(MenuPageRequest request);
    /**
     * 更新菜单。
     */
    MenuResponseVO update(Long id, MenuUpdateRequest request);
    /**
     * 删除菜单。
     */
    void delete(Long id);
}
