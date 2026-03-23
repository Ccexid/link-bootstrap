package me.link.bootstrap.system.tenant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.link.bootstrap.system.tenant.entity.SystemTenantPackage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租户套餐 Mapper 接口
 */
@Mapper
public interface SystemTenantPackageMapper extends BaseMapper<SystemTenantPackage> {
    // 基础的 CRUD 已经由 BaseMapper 提供
}