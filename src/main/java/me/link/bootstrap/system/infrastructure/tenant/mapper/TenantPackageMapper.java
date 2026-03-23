package me.link.bootstrap.system.infrastructure.tenant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.link.bootstrap.system.domain.tenant.entity.TenantPackage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租户套餐 Mapper
 */
@Mapper
public interface TenantPackageMapper extends BaseMapper<TenantPackage> {
}