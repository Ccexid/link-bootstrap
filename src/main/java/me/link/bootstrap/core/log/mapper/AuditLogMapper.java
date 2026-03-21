package me.link.bootstrap.core.log.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.link.bootstrap.core.log.entity.AuditLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审计日志 Mapper 接口
 */
@Mapper
public interface AuditLogMapper extends BaseMapper<AuditLogEntity> {
    // 继承自 BaseMapper，默认拥有 insert/update/select/delete 功能
}