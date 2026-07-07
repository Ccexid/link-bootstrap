package me.link.bootstrap.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.link.bootstrap.infrastructure.persistence.po.CommunityPostPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 社区帖子 Mapper。
 */
@Mapper
public interface CommunityPostMapper extends BaseMapper<CommunityPostPO> {
}
