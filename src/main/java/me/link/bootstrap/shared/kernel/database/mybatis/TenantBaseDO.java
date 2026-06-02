package me.link.bootstrap.shared.kernel.database.mybatis;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 带租户字段的数据库持久化基类，用于需要租户隔离的数据表。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TenantBaseDO extends BaseDO {

    @Serial
    private final static long serialVersionUID = 1L;

    /**
     * 租户 ID
     */
    private Long tenantId;
}
