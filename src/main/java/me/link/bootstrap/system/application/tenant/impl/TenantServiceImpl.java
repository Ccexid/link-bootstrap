package me.link.bootstrap.system.application.tenant.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.system.application.tenant.ITenantService;
import me.link.bootstrap.system.domain.tenant.entity.Tenant;
import me.link.bootstrap.system.infrastructure.tenant.mapper.TenantMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 租户应用服务实现类
 */
@Service
public class TenantServiceImpl extends ServiceImpl<TenantMapper, Tenant> implements ITenantService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTenant(Tenant tenant) {
        // 获取上级租户以计算路径
        if (tenant.getParentId() != null && tenant.getParentId() != 0L) {
            Tenant parent = getById(tenant.getParentId());
            tenant.computePath(parent);
        } else {
            tenant.computePath(null);
        }

        // 使用 MyBatis-Plus 保存
        save(tenant);
        return tenant.getId();
    }
}