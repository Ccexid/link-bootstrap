package me.link.bootstrap.infrastructure.persistence.internal.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.infrastructure.persistence.internal.RoleMenuInternalService;
import me.link.bootstrap.infrastructure.persistence.mapper.RoleMenuMapper;
import me.link.bootstrap.infrastructure.persistence.po.RoleMenuPO;
import org.springframework.stereotype.Service;

@Service
public class RoleMenuInternalServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenuPO> implements RoleMenuInternalService {
}
