package me.link.bootstrap.infrastructure.persistence.internal.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.infrastructure.persistence.internal.TenantInternalService;
import me.link.bootstrap.infrastructure.persistence.mapper.TenantMapper;
import me.link.bootstrap.infrastructure.persistence.po.TenantPO;
import org.springframework.stereotype.Service;

@Service
public class TenantInternalServiceImpl  extends ServiceImpl<TenantMapper, TenantPO> implements TenantInternalService {
}
