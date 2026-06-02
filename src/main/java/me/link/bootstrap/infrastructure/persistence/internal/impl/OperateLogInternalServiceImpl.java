package me.link.bootstrap.infrastructure.persistence.internal.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.infrastructure.persistence.internal.OperateLogInternalService;
import me.link.bootstrap.infrastructure.persistence.mapper.OperateLogMapper;
import me.link.bootstrap.infrastructure.persistence.po.OperateLogPO;
import org.springframework.stereotype.Service;

@Service
public class OperateLogInternalServiceImpl extends ServiceImpl<OperateLogMapper, OperateLogPO> implements OperateLogInternalService {
}
