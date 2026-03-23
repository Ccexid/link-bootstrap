package me.link.bootstrap.core.log.aspect;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.link.bootstrap.core.log.annotation.Log;
import me.link.bootstrap.core.log.model.AuditLogDTO;
import me.link.bootstrap.core.log.model.FieldChangeDetail;
import me.link.bootstrap.core.log.spi.AuditLogStorageProvider;
import me.link.bootstrap.core.tenant.TenantContextHolder;
import me.link.bootstrap.core.utils.BeanDiffUtils;
import me.link.bootstrap.core.utils.SystemClock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executor;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditLogAspect {

    private final ApplicationContext applicationContext;
    private final ExpressionParser parser = new SpelExpressionParser();
    private final Executor auditLogExecutor;

    @Around("@annotation(auditLog)")
    public Object doAround(ProceedingJoinPoint joinPoint, Log auditLog) throws Throwable {
        // 1. 初始解析 BusinessId
        String businessId = parseSpel(joinPoint, auditLog.businessId(), null);
        long startNano = SystemClock.now();

        // 2. 【核心改进】执行前：获取数据库中的旧数据快照
        Object oldData = captureDataSnapshot(auditLog.serviceName(), businessId);

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            // 业务失败：动态解析操作描述并记录失败日志
            String op = parseSpel(joinPoint, auditLog.operation(), null);
            asyncSave(auditLog, op, businessId, oldData, null, startNano, e);
            throw e;
        }

        // 3. 【核心改进】执行后：再次解析 BusinessId (应对新增场景获取自增ID)
        if ("N/A".equals(businessId) || businessId.isBlank()) {
            businessId = parseSpel(joinPoint, auditLog.businessId(), result);
        }

        // 4. 【核心改进】执行后：从数据库获取更新后的真实新数据
        Object newData = captureDataSnapshot(auditLog.serviceName(), businessId);

        // 5. 动态解析 Operation (支持引用 #result)
        String dynamicOp = parseSpel(joinPoint, auditLog.operation(), result);

        // 6. 异步下发存储
        asyncSave(auditLog, dynamicOp, businessId, oldData, newData, startNano, null);

        return result;
    }

    private Object captureDataSnapshot(String serviceName, String id) {
        if (serviceName.isEmpty() || "N/A".equals(id) || id.isBlank()) return null;
        try {
            Object service = applicationContext.getBean(serviceName);
            // 约定：Service 必须实现 getById(Serializable id)
            Method method = service.getClass().getMethod("getById", Serializable.class);
            Object rawData = method.invoke(service, id);
            // 深拷贝一份，防止对象引用导致 diff 失败
            return rawData != null ? BeanUtil.toBean(rawData, rawData.getClass()) : null;
        } catch (Exception ex) {
            log.warn("[AuditLog] 无法获取数据快照, Service: {}, ID: {}", serviceName, id);
            return null;
        }
    }

    private void asyncSave(Log anno, String op, String bId, Object oldD, Object newD, long start, Throwable e) {
        auditLogExecutor.execute(() -> {
            try {
                long costTimeMs = (SystemClock.now() - start) / 1_000;

                // 执行 Diff 对比
                List<FieldChangeDetail> changes = (anno.isDiff() && oldD != null && newD != null)
                        ? BeanDiffUtils.diff(oldD, newD)
                        : null;

                AuditLogDTO logDTO = AuditLogDTO.builder()
                        .tenantId(TenantContextHolder.getTenantId())
                        .module(anno.module())
                        .operation(op)
                        .businessId(bId)
                        .costTime(costTimeMs + "ms")
                        .status(e == null ? "SUCCESS" : "FAIL")
                        .errorMsg(e != null ? e.getMessage() : null)
                        .changes(changes)
                        .createTime(LocalDateTime.now())
                        .build();

                AuditLogStorageProvider.getStorages().forEach(s -> s.save(logDTO));
            } catch (Exception ex) {
                log.error("[AuditLog] 异步保存失败", ex);
            }
        });
    }

    private String parseSpel(ProceedingJoinPoint joinPoint, String spel, Object result) {
        if (spel == null || spel.isBlank()) return "N/A";
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            EvaluationContext context = new StandardEvaluationContext();

            // 绑定参数变量
            Object[] args = joinPoint.getArgs();
            String[] paramNames = signature.getParameterNames();
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
            // 绑定返回值变量 #result
            if (result != null) {
                context.setVariable("result", result);
            }

            Object value = parser.parseExpression(spel).getValue(context);
            return value != null ? value.toString() : spel;
        } catch (Exception e) {
            return spel; // 解析失败则返回原字符串
        }
    }
}