package me.link.bootstrap.shared.kernel.constant;

/**
 * 安全相关常量:Sa-Token Session 键、特权角色码等。
 */
public interface SecurityConstants {

    /* ==================== Sa-Token Session Key ==================== */

    /** Session 中存储租户 ID 的键。 */
    String SESSION_KEY_TENANT_ID = "tenantId";

    /** Session 中存储用户类型(1供应商 2平台 3商家 4用户)的键。 */
    String SESSION_KEY_USER_TYPE = "userType";

    /** Session 中标记当前用户是否平台超管的键(boolean)。 */
    String SESSION_KEY_SUPER_ADMIN = "isSuperAdmin";

    /* ==================== 特权角色 ==================== */

    /**
     * 平台超级管理员角色编码,对应 system_role.code。
     * <p>持有该角色的用户:</p>
     * <ul>
     *   <li>{@link me.link.bootstrap.shared.kernel.database.mybatis.LinkTenantLineHandler}
     *       会让其 SELECT/UPDATE/DELETE 跳过 tenant_id 过滤,实现跨租户访问;</li>
     *   <li>INSERT 时 tenant_id 不会被自动改写,仍以 PO 中显式设置的值落库,
     *       未显式设置时由数据库默认值(0,代表平台租户)兜底。</li>
     * </ul>
     */
    String ROLE_SUPER_ADMIN = "super_admin";
}
