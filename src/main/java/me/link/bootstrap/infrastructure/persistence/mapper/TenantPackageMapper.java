package me.link.bootstrap.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.link.bootstrap.infrastructure.persistence.po.TenantPackagePO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TenantPackageMapper extends BaseMapper<TenantPackagePO> {
}
