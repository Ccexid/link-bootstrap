package me.link.bootstrap.shared.kernel.database.mybatis;

import lombok.extern.slf4j.Slf4j;

/**
 * 租户上下文持有器（基于 ThreadLocal）。
 * <p>
 * 配合 {@link com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor} 与
 * {@link LinkTenantLineHandler} 一起使用：当当前线程标记为忽略时，多租户插件不会
 * 向 SQL 注入 {@code tenant_id} 条件，允许跨租户的查询/写入（用于登录、超管运维、
 * 系统定时任务等显式无租户上下文的场景）。
 * </p>
 * <p>
 * <b>使用约定</b>：必须严格按 try/finally 配对调用 {@link #ignore()} 与 {@link #clear()}，
 * 否则会污染线程池中的后续请求。推荐通过 {@link TenantIgnore @TenantIgnore} 注解 + AOP
 * 自动管理生命周期，避免手工写漏。
 * </p>
 */
@Slf4j
public final class TenantContextHolder {

    private static final ThreadLocal<Boolean> IGNORE = ThreadLocal.withInitial(() -> Boolean.FALSE);

    /**
     * 创建租户ContextHolder实例。
     */
    private TenantContextHolder() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }

    /**
     * 标记当前线程忽略租户过滤。
     */
    public static void ignore() {
        IGNORE.set(Boolean.TRUE);
    }

    /**
     * 判断当前线程是否处于忽略租户过滤模式。
     *
     * @return true-忽略, false-启用租户过滤
     */
    public static boolean isIgnore() {
        Boolean value = IGNORE.get();
        return Boolean.TRUE.equals(value);
    }

    /**
     * 清理 ThreadLocal 状态。
     * <p>
     * 务必在 finally 块中调用，避免线程复用时残留状态导致跨请求泄露。
     * </p>
     */
    public static void clear() {
        IGNORE.remove();
    }
}
