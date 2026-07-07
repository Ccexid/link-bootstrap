package me.link.bootstrap.application.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.application.service.CommunityPostCollectService;
import me.link.bootstrap.infrastructure.persistence.mapper.CommunityPostCollectMapper;
import me.link.bootstrap.infrastructure.persistence.po.CommunityPostCollectPO;
import org.springframework.stereotype.Service;

@Service
public class CommunityPostCollectServiceImpl
        extends ServiceImpl<CommunityPostCollectMapper, CommunityPostCollectPO>
        implements CommunityPostCollectService {
}
