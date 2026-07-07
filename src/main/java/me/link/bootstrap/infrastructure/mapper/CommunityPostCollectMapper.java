package me.link.bootstrap.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.link.bootstrap.infrastructure.persistence.po.CommunityPostCollectPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 社区帖子收藏 Mapper。
 */
@Mapper
public interface CommunityPostCollectMapper extends BaseMapper<CommunityPostCollectPO> {
}
