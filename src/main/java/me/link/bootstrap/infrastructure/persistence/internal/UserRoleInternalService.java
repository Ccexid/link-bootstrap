package me.link.bootstrap.infrastructure.persistence.internal;

import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.infrastructure.persistence.po.UserRolePO;

/**
 * 用户角色关联持久化内部服务，封装 MyBatis-Plus 对用户角色关联表的基础操作能力。
 */
public interface UserRoleInternalService extends IService<UserRolePO> {
}
