package me.link.bootstrap.system.infrastructure.menu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.link.bootstrap.system.domain.menu.entity.Menu;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜单 Mapper 接口
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {
}