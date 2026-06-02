package me.link.bootstrap.infrastructure.persistence.internal;

import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.infrastructure.persistence.po.TenantPO;

/**
 * 租户持久化内部服务，封装 MyBatis-Plus 对租户表的基础操作能力。
 */
public interface TenantInternalService extends IService<TenantPO> {
}
