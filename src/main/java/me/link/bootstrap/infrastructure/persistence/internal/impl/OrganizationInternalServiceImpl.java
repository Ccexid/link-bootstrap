package me.link.bootstrap.infrastructure.persistence.internal.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.infrastructure.persistence.internal.OrganizationInternalService;
import me.link.bootstrap.infrastructure.persistence.mapper.OrganizationMapper;
import me.link.bootstrap.infrastructure.persistence.po.OrganizationPO;
import org.springframework.stereotype.Service;

@Service
public class OrganizationInternalServiceImpl extends ServiceImpl<OrganizationMapper, OrganizationPO> implements OrganizationInternalService {
}
