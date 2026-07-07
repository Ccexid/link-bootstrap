package me.link.bootstrap.application.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.infrastructure.persistence.po.TenantPO;
import java.util.Set;
import me.link.bootstrap.infrastructure.persistence.po.TenantPO;
import me.link.bootstrap.interfaces.dto.request.tenant.TenantCreateRequest;
import me.link.bootstrap.interfaces.dto.request.tenant.TenantPageRequest;
import me.link.bootstrap.interfaces.dto.request.tenant.TenantUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.vo.TenantResponseVO;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;

public interface TenantService extends IService<TenantPO> {

    TenantResponseVO create(TenantCreateRequest request);
    TenantResponseVO get(Long id);
    PageResult<TenantResponseVO> page(TenantPageRequest request);
    TenantResponseVO update(Long id, TenantUpdateRequest request);
    void delete(Long id);
}
