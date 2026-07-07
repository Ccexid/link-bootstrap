package me.link.bootstrap.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.link.bootstrap.infrastructure.persistence.po.UserRolePO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联 MyBatis-Plus Mapper，负责 system_user_role 表的数据库访问。
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRolePO> {
}
