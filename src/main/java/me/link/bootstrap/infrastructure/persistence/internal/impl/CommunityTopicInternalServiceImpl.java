package me.link.bootstrap.infrastructure.persistence.internal.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.infrastructure.persistence.internal.CommunityTopicInternalService;
import me.link.bootstrap.infrastructure.persistence.mapper.CommunityTopicMapper;
import me.link.bootstrap.infrastructure.persistence.po.CommunityTopicPO;
import org.springframework.stereotype.Service;

/**
 * 社区话题持久化服务实现。
 */
@Service
public class CommunityTopicInternalServiceImpl extends ServiceImpl<CommunityTopicMapper, CommunityTopicPO> implements CommunityTopicInternalService {
}
