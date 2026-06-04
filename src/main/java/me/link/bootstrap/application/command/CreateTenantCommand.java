package me.link.bootstrap.application.command;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 创建租户命令对象，封装创建新租户所需的数据。
 * <p>
 * 包含租户名称、联系人信息、关联域名、套餐分配、过期时间和账号数量，
 * 供租户应用服务在创建操作时使用。
 */
public record CreateTenantCommand(
        String name,
        Long contactUserId,
        String contactName,
        String contactMobile,
        Set<String> websites,
        Long packageId,
        LocalDateTime expireTime,
        Integer accountCount
) {
}
