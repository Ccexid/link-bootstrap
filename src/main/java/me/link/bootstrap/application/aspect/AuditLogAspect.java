package me.link.bootstrap.application.aspect;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.link.bootstrap.infrastructure.annotation.Log;
import me.link.bootstrap.infrastructure.context.TenantContextHolder;
import me.link.bootstrap.infrastructure.utils.BeanDiffUtils;
import me.link.bootstrap.infrastructure.utils.SpelUtils;
import me.link.bootstrap.infrastructure.context.SpringContextHolder;
import org.apache.commons.lang3.math.NumberUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * 审计日志切面 (DDD Application 层)
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditLogAspect {

    private final Executor logExecutor;

    @Around("@annotation(auditLog)")
    public Object doAround(ProceedingJoinPoint joinPoint, Log auditLog) throws Throwable {
        // 1. 获取主线程上下文 (在主线程提取，防止异步丢失)
        String currentTenantId = TenantContextHolder.getTenantId();
        long startTime = System.currentTimeMillis();

        // 2. 初始解析 BusinessId
        String businessId = SpelUtils.parse(joinPoint, auditLog.businessId(), null);

        // 3. 前置快照 (仅在需要 Diff 时查询，减少 IO)
        Object oldData = null;
        if (auditLog.isDiff() && !"N/A".equals(businessId)) {
            oldData = captureDataSnapshot(auditLog.serviceName(), businessId);
        }

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            // 异常分支记录
            String op = SpelUtils.parse(joinPoint, auditLog.operation(), null);
            asyncSave(auditLog, op, businessId, oldData, null, startTime, currentTenantId, e);
            throw e;
        }

        // 4. 后置处理：如果是新增场景，从返回值重新解析 ID
        String finalBusinessId = businessId;
        if ("N/A".equals(businessId) || businessId.isBlank()) {
            finalBusinessId = SpelUtils.parse(joinPoint, auditLog.businessId(), Map.of("result", result));
        }

        // 5. 后置快照 (只有开启 Diff 才进行二次查询)
        Object newData = null;
        if (auditLog.isDiff()) {
            newData = captureDataSnapshot(auditLog.serviceName(), finalBusinessId);
        }

        // 6. 动态解析描述
        String dynamicOp = SpelUtils.parse(joinPoint, auditLog.operation(), Map.of("result", result));

        // 7. 异步提交
        asyncSave(auditLog, dynamicOp, finalBusinessId, oldData, newData, startTime, currentTenantId, null);

        return result;
    }

    private void asyncSave(Log anno, String op, String bId, Object oldD, Object newD,
                           long start, String tenantId, Throwable e) {

        // 注意：这里已经通过 AsyncConfig + TtlExecutors 保证了 TraceId 和 Context 的透传
        logExecutor.execute(() -> {
            try {
                long costTime = System.currentTimeMillis() - start;

                // 只有开启了 isDiff 且数据完整才对比
                Object diffChanges = (anno.isDiff() && oldD != null && newD != null)
                        ? BeanDiffUtils.diff(oldD, newD) : null;

                // 组装 DTO (此处省略 DTO 具体定义，建议放在 Domain 层)
                log.info("[AuditLog] Tenant: {}, Module: {}, Op: {}, BusinessId: {}, Cost: {}ms, Status: {}",
                        tenantId, anno.module(), op, bId, costTime, e == null ? "SUCCESS" : "FAIL");

                // TODO: 调用 Domain 层的 Repository 或 StorageProvider 持久化
                // AuditLogDTO dto = ...
                // storageProvider.save(dto);

            } catch (Exception ex) {
                log.error("[AuditLog] 异步记录失败", ex);
            }
        });
    }

    private Object captureDataSnapshot(String serviceName, String id) {
        if (serviceName.isEmpty() || "N/A".equals(id)) return null;
        try {
            // 使用之前优化的 SpringContextHolder 获取 Bean
            Object service = SpringContextHolder.getBean(serviceName);
            Method method = service.getClass().getMethod("getById", Serializable.class);
            Object rawData = method.invoke(service, id);

            // 深拷贝：防止业务逻辑在内存中修改了该对象导致 Diff 失效
            return rawData != null ? BeanUtil.toBean(rawData, rawData.getClass()) : null;
        } catch (Exception ex) {
            log.debug("[AuditLog] 跳过快照捕获: {}", ex.getMessage());
            return null;
        }
    }
}