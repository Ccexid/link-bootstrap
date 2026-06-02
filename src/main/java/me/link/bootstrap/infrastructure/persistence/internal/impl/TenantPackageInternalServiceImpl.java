package me.link.bootstrap.infrastructure.persistence.internal.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.infrastructure.persistence.internal.TenantPackageInternalService;
import me.link.bootstrap.infrastructure.persistence.mapper.TenantPackageMapper;
import me.link.bootstrap.infrastructure.persistence.po.TenantPackagePO;
import org.springframework.stereotype.Service;

@Service
public class TenantPackageInternalServiceImpl extends ServiceImpl<TenantPackageMapper, TenantPackagePO> implements TenantPackageInternalService {
}
