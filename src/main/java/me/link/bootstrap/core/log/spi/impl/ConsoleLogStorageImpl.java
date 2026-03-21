package me.link.bootstrap.core.log.spi.impl;

import lombok.extern.slf4j.Slf4j;
import me.link.bootstrap.core.log.model.AuditLogDTO;
import me.link.bootstrap.core.log.spi.AuditLogStorage;

/**
 * 控制台审计日志存储实现类
 * 将审计日志信息打印到控制台，主要用于开发调试环境
 */
@Slf4j
public class ConsoleLogStorageImpl implements AuditLogStorage {

    /**
     * 保存审计日志到控制台
     *
     * @param logDTO 包含审计日志信息的传输对象
     */
    @Override
    public void save(AuditLogDTO logDTO) {
        // 记录审计日志信息到控制台
        // 使用占位符 {} 格式化输出，依次填入模块名、操作类型、业务ID和变更数量
        log.info("[审计] 模块: {}, 操作: {}, ID: {}, 变更数: {}",
                // 获取日志对应的模块名称
                logDTO.getModule(),
                // 获取具体的操作类型（如：创建、更新、删除等）
                logDTO.getOperation(),
                // 获取关联的业务实体唯一标识
                logDTO.getBusinessId(),
                // 获取变更数据的数量，如果变更数据为null则返回0，避免空指针异常
                logDTO.getChanges() != null ? logDTO.getChanges().size() : 0
        );
    }
}