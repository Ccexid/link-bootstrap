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

    /**
     * 创建用户角色。
     */
    UserRoleResponseVO create(UserRoleCreateRequest request);
    /**
     * 查询用户角色详情。
     */
    UserRoleResponseVO get(Long id);
    /**
     * 分页查询用户角色列表。
     */
    PageResult<UserRoleResponseVO> page(UserRolePageRequest request);
    /**
     * 更新用户角色。
     */
    UserRoleResponseVO update(Long id, UserRoleUpdateRequest request);
    /**
     * 分配。
     */
    void assign(UserRoleAssignRequest request);
    /**
     * 删除用户角色。
     */
    void delete(Long id);
}
