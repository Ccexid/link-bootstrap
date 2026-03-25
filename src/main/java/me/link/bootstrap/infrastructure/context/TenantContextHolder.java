package me.link.bootstrap.infrastructure.context;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Supplier;

/**
 * 租户上下文持有者 (Tenant Context Holder)
 * <p>
 * 核心职责：
 * 1. 存储当前请求的租户 ID，支撑 MyBatis-Plus 等插件的数据隔离。
 * 2. 使用 TransmittableThreadLocal 确保在线程池异步场景下（如 @Async）上下文不丢失。
 * 3. 提供与 Sa-Token 的解耦同步机制。
 */
@Slf4j
public class TenantContextHolder {

    /**
     * 租户 ID 存储
     */
    private static final ThreadLocal<String> TENANT_ID = new TransmittableThreadLocal<>();

    /**
     * 忽略租户隔离标记 (默认不忽略)
     */
    private static final ThreadLocal<Boolean> IGNORE_TENANT = new TransmittableThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return Boolean.FALSE;
        }
    };

    /**
     * 设置租户 ID
     * @param tenantId 租户标识
     */
    public static void setTenantId(String tenantId) {
        if (StringUtils.isNotBlank(tenantId)) {
            TENANT_ID.set(tenantId);
        }
    }

    /**
     * 获取当前租户 ID
     * @return 租户 ID，若无则返回 null
     */
    public static String getTenantId() {
        return TENANT_ID.get();
    }

    /**
     * 【高级集成】从 Sa-Token 会话中同步租户信息
     * 建议在拦截器 (Interceptor) 中调用。
     * 逻辑：从 Sa-Token 的 Session 中提取预存的 tenantId。
     */
    public static void syncFromSaToken() {
        try {
            // 仅在已登录状态下尝试同步
            if (StpUtil.isLogin()) {
                // 约定：登录时已执行过 StpUtil.getSession().set("tenantId", "xxx")
                Object tenantId = StpUtil.getSession().get("tenantId");
                if (tenantId != null) {
                    setTenantId(tenantId.toString());
                }
            }
        } catch (Exception e) {
            log.warn("[TenantContext] 同步 Sa-Token 租户信息失败: {}", e.getMessage());
        }
    }

    /**
     * 设置是否忽略租户过滤
     * @param ignore true 为忽略
     */
    public static void setIgnore(boolean ignore) {
        IGNORE_TENANT.set(ignore);
    }

    /**
     * 当前是否处于忽略状态
     */
    public static boolean isIgnore() {
        return Boolean.TRUE.equals(IGNORE_TENANT.get());
    }

    /**
     * 【语法糖】在忽略租户隔离的环境下执行逻辑
     * 常用于：系统级汇总统计、跨租户数据同步任务
     * * @param supplier 业务逻辑块
     * @return 执行结果
     */
    public static <T> T runWithIgnore(Supplier<T> supplier) {
        Boolean oldIgnore = IGNORE_TENANT.get();
        try {
            IGNORE_TENANT.set(Boolean.TRUE);
            return supplier.get();
        } finally {
            // 必须在 finally 中还原，防止线程池污染
            IGNORE_TENANT.set(oldIgnore);
        }
    }

    /**
     * 清理当前线程的所有上下文
     * 必须在：Interceptor 的 afterCompletion 或异步线程的 finally 块中调用
     */
    public static void clear() {
        TENANT_ID.remove();
        IGNORE_TENANT.remove();
    }
}