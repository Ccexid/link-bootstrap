package me.link.bootstrap.application.command;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 更新租户命令对象，封装更新现有租户所需的数据。
 *
 * 包含租户编号、联系人信息、关联域名、套餐分配、过期时间、账号数量和启用状态，
 * 供租户应用服务在更新操作时使用。
 */
public record UpdateTenantCommand(
        Long id,
        String contactName,
        Long contactUserId,
        String contactMobile,
        Set<String> websites,
        Long packageId,
        LocalDateTime expireTime,
        Integer accountCount,
        Boolean enabled
) {
}
