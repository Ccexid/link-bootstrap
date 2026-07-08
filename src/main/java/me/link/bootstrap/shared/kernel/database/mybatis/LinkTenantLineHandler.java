package me.link.bootstrap.shared.kernel.database.mybatis;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import lombok.extern.slf4j.Slf4j;
import me.link.bootstrap.shared.kernel.util.SecurityHelper;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;

import java.util.Locale;
import java.util.Set;

/**
 * MyBatis-Plus 多租户处理器实现。
 * <p>
 * 由 {@link com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor}
 * 在 SQL 解析阶段回调：从 Spring Security 上下文取当前登录用户的租户ID，自动追加
 * {@code WHERE tenant_id = ?} 到所有 SELECT/UPDATE/DELETE/INSERT 语句中（除显式忽略的表外），
 * 实现全局多租户数据隔离，规避水平越权（IDOR）风险。
 * </p>
 * <p>
 * <b>忽略策略</b>：
 * <ol>
 *   <li>租户上下文标记为忽略（{@link TenantContextHolder#isIgnore()}）—— 全部表跳过过滤；</li>
 *   <li>全局表（{@link #GLOBAL_TABLES}）—— 始终跳过（菜单、租户管理本身、租户套餐）。</li>
 * </ol>
 * </p>
 * <p>
 * <b>租户ID缺失（未登录且未忽略）</b>：返回 {@link NullValue}，使 SQL 退化为
 * {@code tenant_id IS NULL} 条件——租户表中不存在该值，相当于查不到任何数据；
 * 此举防止登录失效情形下意外暴露全量数据。同时打印 warn 日志辅助排查。
 * </p>
 */
@Slf4j
public class LinkTenantLineHandler implements TenantLineHandler {

    /**
     * 全局表清单：这些表继承 {@code BaseDO}（不含 tenant_id 列），不参与租户隔离。
     * 表名以小写存储；匹配时统一转小写。
     */
    private static final Set<String> GLOBAL_TABLES = Set.of(
            "system_menu",
            "system_tenant",
            "system_tenant_package"
    );

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression getTenantId() {
        Long tenantId = SecurityHelper.getTenantId();
        if (tenantId == null) {
            log.warn("租户上下文缺失（未登录或 Session 未写入 tenantId），SQL 将退化为 tenant_id IS NULL");
            return new NullValue();
        }
        return new LongValue(tenantId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTenantIdColumn() {
        return "tenant_id";
    }

    /**
     * 忽略策略(按优先级):
     * <ol>
     *   <li>{@link TenantContextHolder#isIgnore()} 为 true(@TenantIgnore 切面进入)—— 全场景放行;</li>
     *   <li>当前登录用户是平台超管({@link SecurityHelper#isSuperAdmin()})—— 全场景放行,实现跨租户运维。
     *       注意 INSERT 时 tenant_id 不会被自动改写,调用方需在 PO 中显式设置目标租户 ID,
     *       未设置时按 DB 默认值(通常 0)落库;</li>
     *   <li>表名在 {@link #GLOBAL_TABLES} 中(菜单/租户/租户套餐)—— 该表始终放行。</li>
     * </ol>
     */
    @Override
    public boolean ignoreTable(String tableName) {
        if (TenantContextHolder.isIgnore()) {
            return true;
        }
        if (SecurityHelper.isSuperAdmin()) {
            return true;
        }
        if (tableName == null) {
            return false;
        }
        return GLOBAL_TABLES.contains(tableName.toLowerCase(Locale.ROOT));
    }
}
