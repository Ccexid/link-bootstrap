package me.link.bootstrap.application.command;

import java.util.Set;

/**
 * 创建租户套餐命令对象，封装创建新租户套餐所需的数据。
 * <p>
 * 包含套餐名称、备注和关联菜单编号，
 * 供租户套餐应用服务在创建操作时使用。
 */
public record CreateTenantPackageCommand(
        String name,
        String remark,
        Set<Long> menuIds
) {
}
