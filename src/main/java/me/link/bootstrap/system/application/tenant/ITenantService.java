package me.link.bootstrap.system.application.tenant;

import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.system.domain.tenant.entity.Tenant;

/**
 * 租户应用服务接口
 */
public interface ITenantService extends IService<Tenant> {
    /**
     * 创建租户并处理层级逻辑
     */
    Long createTenant(Tenant tenant);
}