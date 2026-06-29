package me.link.bootstrap.infrastructure.persistence.internal.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.infrastructure.persistence.internal.CommunityPostLikeInternalService;
import me.link.bootstrap.infrastructure.persistence.mapper.CommunityPostLikeMapper;
import me.link.bootstrap.infrastructure.persistence.po.CommunityPostLikePO;
import org.springframework.stereotype.Service;

/**
 * 社区帖子点赞内部持久化服务实现。
 */
@Service
public class CommunityPostLikeInternalServiceImpl extends ServiceImpl<CommunityPostLikeMapper, CommunityPostLikePO> implements CommunityPostLikeInternalService {
}
