package me.link.bootstrap.core.log.spi.impl;

import lombok.extern.slf4j.Slf4j;
import me.link.bootstrap.core.log.entity.AuditLogEntity;
import me.link.bootstrap.core.log.mapper.AuditLogMapper;
import me.link.bootstrap.core.log.model.AuditLogDTO;
import me.link.bootstrap.core.log.spi.AuditLogStorage;
import me.link.bootstrap.core.utils.SpringContextHolder;
import org.springframework.lang.NonNull;

/**
 * 数据库审计日志存储实现类 (SPI 实现)
 */
@Slf4j
public class DbLogStorageImpl implements AuditLogStorage {

    @Override
    public void save(@NonNull AuditLogDTO logDTO) {
        try {
            // 1. DTO 转换为数据库实体类
            AuditLogEntity entity = AuditLogEntity.builder()
                    .tenantId(logDTO.getTenantId())
                    .module(logDTO.getModule())
                    .operation(logDTO.getOperation())
                    .businessId(logDTO.getBusinessId())
                    .operator(logDTO.getOperator())
                    .costTime(logDTO.getCostTime())
                    .status(logDTO.getStatus())
                    .errorMsg(logDTO.getErrorMsg())
                    .changes(logDTO.getChanges())
                    .createTime(logDTO.getCreateTime())
                    .build();

            // 2. 从 Spring 上下文获取 Mapper (解决 SPI 无法注入 Bean 的问题)
            AuditLogMapper mapper = SpringContextHolder.getBean(AuditLogMapper.class);

            // 3. 执行入库
            mapper.insert(entity);

            log.debug("[审计] 数据库留痕成功: {} - {}", logDTO.getModule(), logDTO.getOperation());
        } catch (Exception e) {
            log.error("[审计] 数据库留痕失败，详情: {}", e.getMessage(), e);
        }
    }
}