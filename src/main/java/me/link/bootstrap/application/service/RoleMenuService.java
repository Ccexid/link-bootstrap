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

    RoleMenuResponseVO create(RoleMenuCreateRequest request);
    RoleMenuResponseVO get(Long id);
    PageResult<RoleMenuResponseVO> page(RoleMenuPageRequest request);
    RoleMenuResponseVO update(Long id, RoleMenuUpdateRequest request);
    void authorize(RoleMenuAuthorizeRequest request);
    void delete(Long id);
}
