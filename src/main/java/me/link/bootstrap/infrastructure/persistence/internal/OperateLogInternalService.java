package me.link.bootstrap.infrastructure.persistence.internal;

import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.infrastructure.persistence.po.OperateLogPO;

/**
 * 操作日志持久化内部服务，封装 MyBatis-Plus 对操作日志表的基础操作能力。
 */
public interface OperateLogInternalService extends IService<OperateLogPO> {
}
