package me.link.bootstrap.infrastructure.persistence.internal.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.infrastructure.persistence.internal.RoleInternalService;
import me.link.bootstrap.infrastructure.persistence.mapper.RoleMapper;
import me.link.bootstrap.infrastructure.persistence.po.RolePO;
import org.springframework.stereotype.Service;

@Service
public class RoleInternalServiceImpl extends ServiceImpl<RoleMapper, RolePO> implements RoleInternalService {
}
