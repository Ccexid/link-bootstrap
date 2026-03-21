package me.link.bootstrap.core.log.spi;

import me.link.bootstrap.core.log.model.AuditLogDTO;

public interface AuditLogStorage {
    /**
     * 执行日志存储逻辑
     * @param auditLog 结构化的审计日志对象
     */
    void save(AuditLogDTO auditLog);
}