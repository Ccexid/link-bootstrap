package me.link.bootstrap.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.link.bootstrap.infrastructure.persistence.po.TenantPackagePO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租户套餐 MyBatis-Plus Mapper，负责 system_tenant_package 表的数据库访问。
 */
@Mapper
public interface TenantPackageMapper extends BaseMapper<TenantPackagePO> {
}
