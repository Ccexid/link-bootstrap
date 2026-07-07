package me.link.bootstrap.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.link.bootstrap.infrastructure.persistence.po.MenuPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜单 MyBatis-Plus Mapper，负责 system_menu 表的数据库访问。
 */
@Mapper
public interface MenuMapper extends BaseMapper<MenuPO> {
}
