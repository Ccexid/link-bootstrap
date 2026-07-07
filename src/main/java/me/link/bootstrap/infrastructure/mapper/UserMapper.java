package me.link.bootstrap.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.link.bootstrap.infrastructure.persistence.po.UserPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 MyBatis-Plus Mapper，负责 system_users 表的数据库访问。
 */
@Mapper
public interface UserMapper extends BaseMapper<UserPO> {
}
