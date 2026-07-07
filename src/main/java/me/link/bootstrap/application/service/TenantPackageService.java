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

    TenantPackageResponseVO create(TenantPackageCreateRequest request);
    TenantPackageResponseVO get(Long id);
    PageResult<TenantPackageResponseVO> page(TenantPackagePageRequest request);
    TenantPackageResponseVO update(Long id, TenantPackageUpdateRequest request);
    void delete(Long id);
}
