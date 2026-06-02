package me.link.bootstrap.infrastructure.persistence.internal.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.infrastructure.persistence.internal.UserRoleInternalService;
import me.link.bootstrap.infrastructure.persistence.mapper.UserRoleMapper;
import me.link.bootstrap.infrastructure.persistence.po.UserRolePO;
import org.springframework.stereotype.Service;

@Service
public class UserRoleInternalServiceImpl extends ServiceImpl<UserRoleMapper, UserRolePO> implements UserRoleInternalService {
}
