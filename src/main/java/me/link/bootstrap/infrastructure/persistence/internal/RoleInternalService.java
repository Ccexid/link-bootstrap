package me.link.bootstrap.infrastructure.persistence.internal;

import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.infrastructure.persistence.po.RolePO;

/**
 * 角色持久化内部服务，封装 MyBatis-Plus 对角色表的基础操作能力。
 */
public interface RoleInternalService extends IService<RolePO> {
}
