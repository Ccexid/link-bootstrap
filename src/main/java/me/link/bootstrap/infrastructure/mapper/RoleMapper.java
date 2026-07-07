package me.link.bootstrap.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.link.bootstrap.infrastructure.persistence.po.RolePO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色 MyBatis-Plus Mapper，负责 system_role 表的数据库访问。
 */
@Mapper
public interface RoleMapper extends BaseMapper<RolePO> {
}
