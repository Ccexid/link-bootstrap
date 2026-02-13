package me.link.sbc.common.util;

import cn.dev33.satoken.stp.StpUtil;

public class UserContext {
    /**
     * 获取当前用户的租户ID。
     * <p>
     * 该方法从 Sa-Token 的 Session 中安全地获取租户ID。如果 Session 中不存在租户ID，
     * 则返回 null；否则将租户ID转换为 Long 类型并返回。
     *
     * @return 租户ID，类型为 Long。如果未找到租户ID，则返回 null。
     */
    public static Long getTenantId() {
        // 从 Sa-Token 的 Session 中安全获取租户ID
        Object tenantId = StpUtil.getSession().get("tenantId");
        return tenantId == null ? null : (Long) tenantId;
    }

}
