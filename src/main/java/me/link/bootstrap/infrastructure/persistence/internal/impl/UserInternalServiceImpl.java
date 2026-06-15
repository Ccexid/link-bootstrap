package me.link.bootstrap.infrastructure.persistence.internal.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.infrastructure.persistence.internal.UserInternalService;
import me.link.bootstrap.infrastructure.persistence.mapper.UserMapper;
import me.link.bootstrap.infrastructure.persistence.po.UserPO;
import org.springframework.stereotype.Service;

/**
 * 用户持久化内部服务实现，复用 MyBatis-Plus ServiceImpl 的通用 CRUD 能力。
 */
@Service
public class UserInternalServiceImpl extends ServiceImpl<UserMapper, UserPO> implements UserInternalService {
}
