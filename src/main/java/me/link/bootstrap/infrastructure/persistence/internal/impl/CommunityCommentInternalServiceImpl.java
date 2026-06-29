package me.link.bootstrap.infrastructure.persistence.internal.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.infrastructure.persistence.internal.CommunityCommentInternalService;
import me.link.bootstrap.infrastructure.persistence.mapper.CommunityCommentMapper;
import me.link.bootstrap.infrastructure.persistence.po.CommunityCommentPO;
import org.springframework.stereotype.Service;

/**
 * 社区评论内部持久化服务实现。
 */
@Service
public class CommunityCommentInternalServiceImpl extends ServiceImpl<CommunityCommentMapper, CommunityCommentPO> implements CommunityCommentInternalService {
}
