package me.link.bootstrap.infrastructure.persistence.internal.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.infrastructure.persistence.internal.CommunitySectionInternalService;
import me.link.bootstrap.infrastructure.persistence.mapper.CommunitySectionMapper;
import me.link.bootstrap.infrastructure.persistence.po.CommunitySectionPO;
import org.springframework.stereotype.Service;

/**
 * 社区板块持久化服务实现。
 */
@Service
public class CommunitySectionInternalServiceImpl extends ServiceImpl<CommunitySectionMapper, CommunitySectionPO> implements CommunitySectionInternalService {
}
