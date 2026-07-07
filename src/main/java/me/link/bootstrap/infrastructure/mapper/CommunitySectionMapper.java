package me.link.bootstrap.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.link.bootstrap.infrastructure.persistence.po.CommunitySectionPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 社区板块 Mapper。
 */
@Mapper
public interface CommunitySectionMapper extends BaseMapper<CommunitySectionPO> {
}
