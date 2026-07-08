package me.link.bootstrap.application.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.infrastructure.persistence.po.TenantPackagePO;
import java.util.Set;
import me.link.bootstrap.interfaces.dto.request.tenantpackage.TenantPackageCreateRequest;
import me.link.bootstrap.interfaces.dto.request.tenantpackage.TenantPackagePageRequest;
import me.link.bootstrap.interfaces.dto.request.tenantpackage.TenantPackageUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.vo.TenantPackageResponseVO;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;

public interface TenantPackageService extends IService<TenantPackagePO> {

    /**
     * 创建租户套餐。
     */
    TenantPackageResponseVO create(TenantPackageCreateRequest request);
    /**
     * 查询租户套餐详情。
     */
    TenantPackageResponseVO get(Long id);
    /**
     * 分页查询租户套餐列表。
     */
    PageResult<TenantPackageResponseVO> page(TenantPackagePageRequest request);
    /**
     * 更新租户套餐。
     */
    TenantPackageResponseVO update(Long id, TenantPackageUpdateRequest request);
    /**
     * 删除租户套餐。
     */
    void delete(Long id);
}
