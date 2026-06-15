package me.link.bootstrap.infrastructure.persistence.internal.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.infrastructure.persistence.internal.RoleInternalService;
import me.link.bootstrap.infrastructure.persistence.mapper.RoleMapper;
import me.link.bootstrap.infrastructure.persistence.po.RolePO;
import org.springframework.stereotype.Service;

/**
 * 角色持久化内部服务实现，复用 MyBatis-Plus ServiceImpl 的通用 CRUD 能力。
 */
@Service
public class RoleInternalServiceImpl extends ServiceImpl<RoleMapper, RolePO> implements RoleInternalService {
}
