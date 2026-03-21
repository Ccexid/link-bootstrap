package me.link.bootstrap.core.tenant;

import lombok.extern.slf4j.Slf4j;
import java.util.Optional;

/**
 * 租户上下文持有者
 * 使用 InheritableThreadLocal 确保子线程可以继承父线程的租户ID
 */
@Slf4j
public class TenantContextHolder {

    private static final ThreadLocal<String> TENANT_CONTEXT = new InheritableThreadLocal<>();

    /**
     * 设置租户ID
     */
    public static void setTenantId(String tenantId) {
        log.debug(">>> 设置当前线程租户上下文 ID: {}", tenantId);
        TENANT_CONTEXT.set(tenantId);
    }

    /**
     * 获取租户ID
     */
    public static String getTenantId() {
        return TENANT_CONTEXT.get();
    }

    /**
     * 获取租户ID（若为空则抛出异常，用于强制校验场景）
     */
    public static String getRequiredTenantId() {
        return Optional.ofNullable(TENANT_CONTEXT.get())
                .orElseThrow(() -> new RuntimeException("系统异常：未能获取到当前租户上下文信息"));
    }

    /**
     * 清理上下文
     * 重要：在拦截器结束或线程池任务结束后必须调用，防止内存泄漏
     */
    public static void clear() {
        TENANT_CONTEXT.remove();
    }
}