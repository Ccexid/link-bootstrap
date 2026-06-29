package me.link.bootstrap.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.link.bootstrap.infrastructure.persistence.po.CommunityCommentPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 社区评论 Mapper。
 */
@Mapper
public interface CommunityCommentMapper extends BaseMapper<CommunityCommentPO> {
}
