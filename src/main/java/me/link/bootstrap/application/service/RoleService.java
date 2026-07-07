package me.link.bootstrap.application.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.infrastructure.persistence.po.RolePO;
import java.util.Optional;
import me.link.bootstrap.infrastructure.persistence.po.RolePO;
import me.link.bootstrap.interfaces.dto.request.role.RoleCreateRequest;
import me.link.bootstrap.interfaces.dto.request.role.RolePageRequest;
import me.link.bootstrap.interfaces.dto.request.role.RoleUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.vo.RoleResponseVO;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;

public interface RoleService extends IService<RolePO> {

    RoleResponseVO create(RoleCreateRequest request);
    RoleResponseVO get(Long id);
    PageResult<RoleResponseVO> page(RolePageRequest request);
    RoleResponseVO update(Long id, RoleUpdateRequest request);
    void delete(Long id);
}
