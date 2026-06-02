package me.link.bootstrap.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.link.bootstrap.infrastructure.persistence.po.TenantPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租户 MyBatis-Plus Mapper，负责 system_tenant 表的数据库访问。
 */
@Mapper
public interface TenantMapper extends BaseMapper<TenantPO> {
}
