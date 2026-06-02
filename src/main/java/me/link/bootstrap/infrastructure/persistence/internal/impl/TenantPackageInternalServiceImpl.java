package me.link.bootstrap.infrastructure.persistence.internal.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.infrastructure.persistence.internal.TenantPackageInternalService;
import me.link.bootstrap.infrastructure.persistence.mapper.TenantPackageMapper;
import me.link.bootstrap.infrastructure.persistence.po.TenantPackagePO;
import org.springframework.stereotype.Service;

/**
 * 租户套餐持久化内部服务实现，复用 MyBatis-Plus ServiceImpl 的通用 CRUD 能力。
 */
@Service
public class TenantPackageInternalServiceImpl extends ServiceImpl<TenantPackageMapper, TenantPackagePO> implements TenantPackageInternalService {
}
