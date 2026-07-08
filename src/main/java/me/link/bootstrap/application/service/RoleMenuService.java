package me.link.bootstrap.application.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.infrastructure.persistence.po.RoleMenuPO;
import java.util.List;
import me.link.bootstrap.infrastructure.persistence.po.RolePO;
import me.link.bootstrap.interfaces.dto.request.rolemenu.RoleMenuAuthorizeRequest;
import me.link.bootstrap.interfaces.dto.request.rolemenu.RoleMenuCreateRequest;
import me.link.bootstrap.interfaces.dto.request.rolemenu.RoleMenuPageRequest;
import me.link.bootstrap.interfaces.dto.request.rolemenu.RoleMenuUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.vo.RoleMenuResponseVO;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;

public interface RoleMenuService extends IService<RoleMenuPO> {

    /**
     * 创建角色菜单。
     */
    RoleMenuResponseVO create(RoleMenuCreateRequest request);
    /**
     * 查询角色菜单详情。
     */
    RoleMenuResponseVO get(Long id);
    /**
     * 分页查询角色菜单列表。
     */
    PageResult<RoleMenuResponseVO> page(RoleMenuPageRequest request);
    /**
     * 更新角色菜单。
     */
    RoleMenuResponseVO update(Long id, RoleMenuUpdateRequest request);
    /**
     * 授权。
     */
    void authorize(RoleMenuAuthorizeRequest request);
    /**
     * 删除角色菜单。
     */
    void delete(Long id);
}
