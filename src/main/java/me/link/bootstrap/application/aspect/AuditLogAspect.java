package me.link.bootstrap.application.aspect;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.link.bootstrap.domain.log.model.AuditLogDTO;
import me.link.bootstrap.domain.log.model.FieldChangeDetail;
import me.link.bootstrap.domain.log.spi.AuditLogStorage;
import me.link.bootstrap.infrastructure.annotation.Log;
import me.link.bootstrap.infrastructure.context.SpringContextHolder;
import me.link.bootstrap.infrastructure.context.TenantContextHolder;
import me.link.bootstrap.infrastructure.utils.BeanDiffUtils;
import me.link.bootstrap.infrastructure.utils.SpelUtils;
import me.link.bootstrap.infrastructure.utils.TraceUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executor;

/**
 * 审计日志切面 (高性能 & 异步多租户 & 自动 Diff 版本)
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditLogAspect {

    private final Executor logExecutor;
    private final List<AuditLogStorage> storageProviders;

    @Around("@annotation(auditLog)")
    public Object doAround(ProceedingJoinPoint joinPoint, Log auditLog) throws Throwable {
        // 1. 备份主线程上下文 (上下文信息必须在主线程提取)
        String tenantId = TenantContextHolder.getTenantId();
        String traceId = TraceUtils.getTraceId();
        long startTime = System.currentTimeMillis();

        // 2. 初始尝试解析 BusinessId (此时只能解析参数中的变量)
        String businessId = SpelUtils.parse(joinPoint, auditLog.businessId(), null);

        // 3. 获取前置快照 (更新/删除场景)
        Object oldData = null;
        if (auditLog.isDiff() && StrUtil.isNotBlank(businessId) && !"N/A".equals(businessId)) {
            oldData = captureDataSnapshot(auditLog.serviceName(), businessId);
        }

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            // 异常分支记录
            handleLogAsync(auditLog, joinPoint, businessId, oldData, null, startTime, tenantId, traceId, e);
            throw e;
        }

        // 4. 获取后置 BusinessId (支持新增场景：从 result 中提取生成的 ID)
        String finalBusinessId = businessId;
        if (StrUtil.isBlank(businessId) || "N/A".equals(businessId)) {
            finalBusinessId = SpelUtils.parse(joinPoint, auditLog.businessId(), Map.of("result", result));
        }

        // 5. 获取后置快照 (更新/新增场景)
        Object newData = auditLog.isDiff() ? captureDataSnapshot(auditLog.serviceName(), finalBusinessId) : null;

        // 6. 正常记录
        handleLogAsync(auditLog, joinPoint, finalBusinessId, oldData, newData, startTime, tenantId, traceId, null, result);

        return result;
    }

    /**
     * 内部包装方法：准备数据并提交给异步线程池
     */
    private void handleLogAsync(Log anno, ProceedingJoinPoint jp, String bId, Object oldD, Object newD,
                                long start, String tenantId, String traceId, Throwable ex, Object... resultObj) {

        // 1. 在主线程准备 SpEL 变量上下文 (resultObj 可能包含返回值)
        Map<String, Object> vars = new HashMap<>();
        if (ex != null) {
            vars.put("error", ex.getMessage());
            vars.put("status", "FAIL");
        } else if (resultObj.length > 0) {
            vars.put("result", resultObj[0]);
            vars.put("status", "SUCCESS");
        }

        // 2. 解析 Operation 描述 (此时 #result 可用)
        String op = SpelUtils.parse(jp, anno.operation(), vars);

        // 3. 异步提交执行
        logExecutor.execute(() -> {
            try {
                // 还原异步线程上下文
                TenantContextHolder.setTenantId(tenantId);
                TraceUtils.setTraceId(traceId);

                // 计算数据差异 (只有开启了 diff 且新旧快照都不为 null 时计算)
                List<FieldChangeDetail> changes = null;
                if (anno.isDiff() && oldD != null && newD != null) {
                    changes = BeanDiffUtils.diff(oldD, newD);
                }

                // 组装 DTO
                AuditLogDTO dto = AuditLogDTO.builder()
                        .tenantId(StrUtil.isBlank(tenantId) ? "0" : tenantId)
                        .traceId(traceId)
                        .module(anno.module())
                        .operation(op)
                        .businessId(bId)
                        .costTime(System.currentTimeMillis() - start)
                        .status(ex == null ? "SUCCESS" : "FAIL")
                        .errorMsg(ex != null ? ex.getMessage() : null)
                        .changes(changes)
                        .createTime(LocalDateTime.now())
                        .build();

                // 遍历存储器执行保存
                saveToStorages(dto);

            } catch (Exception e) {
                log.error("[AuditLog] 异步处理流程发生异常", e);
            } finally {
                // 必须清理，防止线程池污染
                TenantContextHolder.clear();
                TraceUtils.clear();
            }
        });
    }

    private void saveToStorages(AuditLogDTO dto) {
        storageProviders.stream()
                .filter(AuditLogStorage::isEnabled)
                .sorted(Comparator.comparingInt(AuditLogStorage::getOrder))
                .forEach(storage -> {
                    try {
                        storage.record(dto);
                        log.debug("[AuditLog] 存储器 [{}] 保存成功, TraceId: {}", storage.getName(), dto.getTraceId());
                    } catch (Exception e) {
                        log.error("[AuditLog] 存储器 [{}] 写入失败: {}", storage.getName(), e.getMessage());
                    }
                });
    }

    /**
     * 利用反射获取数据快照
     */
    private Object captureDataSnapshot(String serviceName, String id) {
        if (StrUtil.hasBlank(serviceName, id) || "N/A".equals(id)) return null;

        try {
            Object service = SpringContextHolder.getBean(serviceName);
            Class<?> targetClass = AopProxyUtils.ultimateTargetClass(service);

            // --- 策略升级：先按名称找所有方法，再匹配单参数的那个 ---
            Method method = Arrays.stream(targetClass.getDeclaredMethods())
                    .filter(m -> "getById".equals(m.getName()) && m.getParameterCount() == 1)
                    .findFirst()
                    .orElse(null);

            if (method == null) {
                log.warn("[AuditLog] 无法在类 {} 中定位到 getById(id) 方法", targetClass.getName());
                return null;
            }

            // 确保方法可访问（即使是 public，在某些 ClassLoader 下也建议 setAccessible）
            method.setAccessible(true);

            // 类型强制转换：将 String 类型的 id 转换为方法定义的实际参数类型 (如 Long)
            Class<?> paramType = method.getParameterTypes()[0];
            Object convertedId = Convert.convert(paramType, id);

            // 注意：这里调用 service (代理对象)，而不是 target (原始对象)
            Object data = method.invoke(service, convertedId);

            return data != null ? BeanUtil.toBean(data, data.getClass()) : null;
        } catch (Exception e) {
            log.error("[AuditLog] 获取快照异常: ", e);
            return null;
        }
    }
}