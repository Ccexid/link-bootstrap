package me.link.bootstrap.application.command;

/**
 * 登录命令。
 * <p>多租户场景下 (username, tenant_id) 联合唯一,前端必须显式传入 tenantId 才能定位用户。</p>
 */
public record LoginCommand(String username, String password, Long tenantId) {
}
