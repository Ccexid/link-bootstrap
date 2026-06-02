package me.link.bootstrap.infrastructure.persistence.internal;

import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.infrastructure.persistence.po.TenantPackagePO;

/**
 * 租户套餐持久化内部服务，封装 MyBatis-Plus 对套餐表的基础操作能力。
 */
public interface TenantPackageInternalService extends IService<TenantPackagePO> {
}
