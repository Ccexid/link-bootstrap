package me.link.bootstrap.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.link.bootstrap.infrastructure.persistence.po.CommunityTopicPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 社区话题 Mapper。
 */
@Mapper
public interface CommunityTopicMapper extends BaseMapper<CommunityTopicPO> {
}
