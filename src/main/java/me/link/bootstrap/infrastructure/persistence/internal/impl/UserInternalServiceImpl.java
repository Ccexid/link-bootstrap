package me.link.bootstrap.infrastructure.persistence.internal.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.infrastructure.persistence.internal.UserInternalService;
import me.link.bootstrap.infrastructure.persistence.mapper.UserMapper;
import me.link.bootstrap.infrastructure.persistence.po.UserPO;
import org.springframework.stereotype.Service;

@Service
public class UserInternalServiceImpl extends ServiceImpl<UserMapper, UserPO> implements UserInternalService {
}
