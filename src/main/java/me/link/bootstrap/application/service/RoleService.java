package me.link.bootstrap.application.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.infrastructure.persistence.po.RolePO;
import java.util.Optional;
import me.link.bootstrap.interfaces.dto.request.role.RoleCreateRequest;
import me.link.bootstrap.interfaces.dto.request.role.RolePageRequest;
import me.link.bootstrap.interfaces.dto.request.role.RoleUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.vo.RoleResponseVO;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;

public interface RoleService extends IService<RolePO> {

    /**
     * 创建角色。
     */
    RoleResponseVO create(RoleCreateRequest request);
    /**
     * 查询角色详情。
     */
    RoleResponseVO get(Long id);
    /**
     * 分页查询角色列表。
     */
    PageResult<RoleResponseVO> page(RolePageRequest request);
    /**
     * 更新角色。
     */
    RoleResponseVO update(Long id, RoleUpdateRequest request);
    /**
     * 删除角色。
     */
    void delete(Long id);
}
