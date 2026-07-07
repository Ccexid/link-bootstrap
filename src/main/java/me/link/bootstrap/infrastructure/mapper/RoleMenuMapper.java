package me.link.bootstrap.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.link.bootstrap.infrastructure.persistence.po.RoleMenuPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色菜单关联 MyBatis-Plus Mapper，负责 system_role_menu 表的数据库访问。
 */
@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenuPO> {
}
