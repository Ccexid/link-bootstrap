package me.link.bootstrap.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.link.bootstrap.infrastructure.persistence.po.OrganizationPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 组织 MyBatis-Plus Mapper，负责 system_organization 表的数据库访问。
 */
@Mapper
public interface OrganizationMapper extends BaseMapper<OrganizationPO> {
}
