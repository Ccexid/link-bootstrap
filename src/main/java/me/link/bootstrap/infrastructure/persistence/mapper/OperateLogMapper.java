package me.link.bootstrap.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.link.bootstrap.infrastructure.persistence.po.OperateLogPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志 MyBatis-Plus Mapper，负责 system_operate_log 表的数据库访问。
 */
@Mapper
public interface OperateLogMapper extends BaseMapper<OperateLogPO> {
}
