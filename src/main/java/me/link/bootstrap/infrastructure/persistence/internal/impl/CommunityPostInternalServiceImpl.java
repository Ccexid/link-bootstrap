package me.link.bootstrap.infrastructure.persistence.internal.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.infrastructure.persistence.internal.CommunityPostInternalService;
import me.link.bootstrap.infrastructure.persistence.mapper.CommunityPostMapper;
import me.link.bootstrap.infrastructure.persistence.po.CommunityPostPO;
import org.springframework.stereotype.Service;

/**
 * 社区帖子持久化服务实现。
 */
@Service
public class CommunityPostInternalServiceImpl extends ServiceImpl<CommunityPostMapper, CommunityPostPO> implements CommunityPostInternalService {
}
