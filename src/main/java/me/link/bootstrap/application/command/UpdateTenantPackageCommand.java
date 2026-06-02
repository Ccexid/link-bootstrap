package me.link.bootstrap.application.command;

import java.util.Set;

/**
 * 更新租户套餐命令对象，封装更新现有租户套餐所需的数据。
 * <p>
 * 包含套餐编号、名称、备注、关联菜单编号和启用状态，
 * 供租户套餐应用服务在更新操作时使用。
 */
public record UpdateTenantPackageCommand(
        Long id,
        String name,
        String remark,
        Set<Long> menuIds,
        Boolean enabled
) {
}
