package me.link.bootstrap.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.link.bootstrap.infrastructure.persistence.po.CommunityPostLikePO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 社区帖子点赞 Mapper。
 */
@Mapper
public interface CommunityPostLikeMapper extends BaseMapper<CommunityPostLikePO> {
}
