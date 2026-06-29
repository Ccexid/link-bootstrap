package me.link.bootstrap.infrastructure.persistence.internal.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.infrastructure.persistence.internal.CommunityPostCollectInternalService;
import me.link.bootstrap.infrastructure.persistence.mapper.CommunityPostCollectMapper;
import me.link.bootstrap.infrastructure.persistence.po.CommunityPostCollectPO;
import org.springframework.stereotype.Service;

/**
 * 社区帖子收藏内部持久化服务实现。
 */
@Service
public class CommunityPostCollectInternalServiceImpl extends ServiceImpl<CommunityPostCollectMapper, CommunityPostCollectPO> implements CommunityPostCollectInternalService {
}
