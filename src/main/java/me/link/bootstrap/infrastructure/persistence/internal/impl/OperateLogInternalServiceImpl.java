package me.link.bootstrap.infrastructure.persistence.internal.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.infrastructure.persistence.internal.OperateLogInternalService;
import me.link.bootstrap.infrastructure.persistence.mapper.OperateLogMapper;
import me.link.bootstrap.infrastructure.persistence.po.OperateLogPO;
import org.springframework.stereotype.Service;

/**
 * 操作日志持久化内部服务实现，复用 MyBatis-Plus ServiceImpl 的通用 CRUD 能力。
 */
@Service
public class OperateLogInternalServiceImpl extends ServiceImpl<OperateLogMapper, OperateLogPO> implements OperateLogInternalService {
}
