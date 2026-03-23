package me.link.bootstrap.system.infrastructure.tenant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.link.bootstrap.system.domain.tenant.entity.Tenant;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租户 Mapper 接口
 */
@Mapper
public interface TenantMapper extends BaseMapper<Tenant> {
}