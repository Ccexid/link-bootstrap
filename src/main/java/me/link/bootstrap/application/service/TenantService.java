package me.link.bootstrap.application.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.infrastructure.persistence.po.TenantPO;
import java.util.Set;
import me.link.bootstrap.interfaces.dto.request.tenant.TenantCreateRequest;
import me.link.bootstrap.interfaces.dto.request.tenant.TenantPageRequest;
import me.link.bootstrap.interfaces.dto.request.tenant.TenantUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.vo.TenantResponseVO;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;

public interface TenantService extends IService<TenantPO> {

    /**
     * 创建租户。
     */
    TenantResponseVO create(TenantCreateRequest request);
    /**
     * 查询租户详情。
     */
    TenantResponseVO get(Long id);
    /**
     * 分页查询租户列表。
     */
    PageResult<TenantResponseVO> page(TenantPageRequest request);
    /**
     * 更新租户。
     */
    TenantResponseVO update(Long id, TenantUpdateRequest request);
    /**
     * 删除租户。
     */
    void delete(Long id);
}
