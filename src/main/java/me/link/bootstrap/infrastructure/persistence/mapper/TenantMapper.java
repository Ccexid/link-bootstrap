package me.link.bootstrap.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.link.bootstrap.infrastructure.persistence.po.TenantPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TenantMapper extends BaseMapper<TenantPO> {
}
