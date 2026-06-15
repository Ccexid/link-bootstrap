package me.link.bootstrap.infrastructure.persistence.internal.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.infrastructure.persistence.internal.RoleMenuInternalService;
import me.link.bootstrap.infrastructure.persistence.mapper.RoleMenuMapper;
import me.link.bootstrap.infrastructure.persistence.po.RoleMenuPO;
import org.springframework.stereotype.Service;

/**
 * 角色菜单关联持久化内部服务实现，复用 MyBatis-Plus ServiceImpl 的通用 CRUD 能力。
 */
@Service
public class RoleMenuInternalServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenuPO> implements RoleMenuInternalService {
}
