package me.link.bootstrap.application.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.infrastructure.persistence.po.UserRolePO;
import java.util.List;
import me.link.bootstrap.infrastructure.persistence.po.RolePO;
import me.link.bootstrap.infrastructure.persistence.po.UserPO;
import me.link.bootstrap.interfaces.dto.request.userrole.UserRoleAssignRequest;
import me.link.bootstrap.interfaces.dto.request.userrole.UserRoleCreateRequest;
import me.link.bootstrap.interfaces.dto.request.userrole.UserRolePageRequest;
import me.link.bootstrap.interfaces.dto.request.userrole.UserRoleUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.vo.UserRoleResponseVO;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;

public interface UserRoleService extends IService<UserRolePO> {

    UserRoleResponseVO create(UserRoleCreateRequest request);
    UserRoleResponseVO get(Long id);
    PageResult<UserRoleResponseVO> page(UserRolePageRequest request);
    UserRoleResponseVO update(Long id, UserRoleUpdateRequest request);
    void assign(UserRoleAssignRequest request);
    void delete(Long id);
}
