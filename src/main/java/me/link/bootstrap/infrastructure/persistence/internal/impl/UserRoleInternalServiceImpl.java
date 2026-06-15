package me.link.bootstrap.infrastructure.persistence.internal.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.infrastructure.persistence.internal.UserRoleInternalService;
import me.link.bootstrap.infrastructure.persistence.mapper.UserRoleMapper;
import me.link.bootstrap.infrastructure.persistence.po.UserRolePO;
import org.springframework.stereotype.Service;

/**
 * 用户角色关联持久化内部服务实现，复用 MyBatis-Plus ServiceImpl 的通用 CRUD 能力。
 */
@Service
public class UserRoleInternalServiceImpl extends ServiceImpl<UserRoleMapper, UserRolePO> implements UserRoleInternalService {
}
