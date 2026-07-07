package me.link.bootstrap.application.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.application.service.CommunityPostLikeService;
import me.link.bootstrap.infrastructure.mapper.CommunityPostLikeMapper;
import me.link.bootstrap.infrastructure.persistence.po.CommunityPostLikePO;
import org.springframework.stereotype.Service;

@Service
public class CommunityPostLikeServiceImpl
        extends ServiceImpl<CommunityPostLikeMapper, CommunityPostLikePO>
        implements CommunityPostLikeService {
}
