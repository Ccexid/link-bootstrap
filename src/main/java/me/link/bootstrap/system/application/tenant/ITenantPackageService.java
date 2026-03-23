package me.link.bootstrap.system.application.tenant;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.system.domain.tenant.entity.TenantPackage;
import me.link.bootstrap.system.interfaces.tenant.vo.TenantPackagePageReqVO;

/**
 * 租户套餐应用服务接口
 */
public interface ITenantPackageService extends IService<TenantPackage> {

    // 这里可以定义复杂的跨领域编排逻辑
    IPage<TenantPackage> getPackagePage(TenantPackagePageReqVO pageReqVO);
}