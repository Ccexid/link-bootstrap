package me.link.bootstrap.shared.kernel.database.mybatis;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

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
