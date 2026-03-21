package me.link.bootstrap.core.log.aspect;

import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import me.link.bootstrap.core.log.annotation.Log;
import me.link.bootstrap.core.log.model.AuditLogDTO;
import me.link.bootstrap.core.log.model.FieldChangeDetail;
import me.link.bootstrap.core.log.spi.AuditLogStorage;
import me.link.bootstrap.core.tenant.TenantContextHolder;
import me.link.bootstrap.core.utils.BeanDiffUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executor;

/**
 * 审计日志切面类
 * 用于拦截带有 @Log 注解的方法，自动记录操作前后的数据变更、执行状态及耗时等信息
 */
@Aspect
@Component
@Slf4j
public class AuditLogAspect implements ApplicationContextAware {

    // Spring 应用上下文，用于获取 Bean 和依赖服务
    private ApplicationContext applicationContext;
    
    // SpEL 表达式解析器，用于动态解析注解中的表达式（如业务 ID）
    private final ExpressionParser parser = new SpelExpressionParser();
    
    // SPI 加载器，用于加载所有实现了 AuditLogStorage 接口的日志存储实现类，支持多存储策略
    private static final ServiceLoader<AuditLogStorage> storageLoader = ServiceLoader.load(AuditLogStorage.class);

    /**
     * 设置 Spring 应用上下文
     * 由 Spring 容器在初始化时调用，用于后续获取 Bean
     */
    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 环绕通知：拦截带有 @Log 注解的方法
     * 
     * @param joinPoint 连接点，包含方法执行的上下文信息
     * @param auditLog 注解实例，包含日志配置信息（模块、操作、业务 ID 表达式等）
     * @return 原始方法的返回值
     * @throws Throwable 方法执行过程中抛出的异常
     */
    @Around("@annotation(auditLog)")
    public Object doAround(ProceedingJoinPoint joinPoint, Log auditLog) throws Throwable {
        // 步骤 1: 解析 SpEL 表达式获取业务 ID（例如从方法参数中提取 #userId）
        String businessId = parseSpel(joinPoint, auditLog.businessId());
        
        // 记录开始时间，用于计算执行耗时
        long startTime = System.currentTimeMillis();

        // 步骤 2: 同步获取修改前的旧数据快照
        // 仅当配置了服务名且业务 ID 有效时才执行
        Object oldData = null;
        if (!auditLog.serviceName().isEmpty() && !businessId.equals("N/A")) {
            // 通过反射调用指定服务的 getById 方法获取旧数据
            oldData = fetchOldData(auditLog.serviceName(), businessId);
            if (oldData != null) {
                // 深拷贝旧数据对象，防止后续业务逻辑修改原对象导致快照失真
                oldData = BeanUtil.toBean(oldData, oldData.getClass());
            }
        }

        Object result;
        try {
            // 步骤 3: 执行原始业务方法
            result = joinPoint.proceed();
        } catch (Throwable e) {
            // 步骤 4: 如果业务方法抛出异常，异步记录失败日志（无新数据）
            asyncSaveLog(auditLog, businessId, oldData, null, startTime, e);
            throw e; // 重新抛出异常，保持原有行为
        }

        // 步骤 5: 获取修改后的新数据
        // 默认取方法第一个参数作为新数据（适用于更新/创建场景）
        Object newData = joinPoint.getArgs().length > 0 ? joinPoint.getArgs()[0] : null;

        // 步骤 6: 异步计算数据差异并保存日志（无论成功与否都记录）
        asyncSaveLog(auditLog, businessId, oldData, newData, startTime, null);

        return result;
    }

    /**
     * 异步保存审计日志
     * 使用线程池异步执行，避免阻塞主业务流程
     * 
     * @param auditLog 日志注解配置
     * @param businessId 业务 ID
     * @param oldData 修改前的数据快照
     * @param newData 修改后的数据
     * @param startTime 开始时间戳
     * @param e 执行过程中的异常（若无则为 null）
     */
    private void asyncSaveLog(Log auditLog, String businessId, Object oldData, Object newData, long startTime, Throwable e) {
        // 获取支持租户感知的异步执行器，确保线程上下文中保留租户信息
        Executor executor = (Executor) applicationContext.getBean("tenantAwareExecutor");
        executor.execute(() -> {
            try {
                // 步骤 1: 计算新旧数据的字段级差异
                List<FieldChangeDetail> diffs = null;
                if (oldData != null && newData != null) {
                    diffs = BeanDiffUtils.diff(oldData, newData);
                }

                // 步骤 2: 构造结构化的日志 DTO 对象
                AuditLogDTO logDTO = AuditLogDTO.builder()
                        .tenantId(TenantContextHolder.getTenantId()) // 当前租户 ID
                        .module(auditLog.module()) // 模块名称（来自注解）
                        .operation(auditLog.operation()) // 操作类型（来自注解）
                        .businessId(businessId) // 业务 ID
                        .costTime((System.currentTimeMillis() - startTime) + "ms") // 执行耗时
                        .status(e == null ? "SUCCESS" : "FAIL") // 执行状态
                        .errorMsg(e != null ? e.getMessage() : null) // 错误信息（如有）
                        .changes(diffs) // 字段变更详情
                        .createTime(LocalDateTime.now()) // 日志创建时间
                        .build();

                // 步骤 3: 通过 SPI 机制调用所有注册的日志存储实现进行保存
                // 支持同时写入数据库、消息队列、搜索引擎等多种存储介质
                storageLoader.forEach(storage -> storage.save(logDTO));

            } catch (Exception ex) {
                // 捕获并记录日志保存过程中的异常，避免影响主业务
                log.error("审计日志记录失败", ex);
            }
        });
    }

    /**
     * 通过反射调用指定服务的 getById 方法获取旧数据
     * 
     * @param serviceName Spring Bean 名称
     * @param id 业务 ID
     * @return 查询到的旧数据对象，若失败则返回 null
     */
    private Object fetchOldData(String serviceName, String id) {
        try {
            // 从 Spring 容器中获取指定名称的服务 Bean
            Object service = applicationContext.getBean(serviceName);
            // 反射获取 getById 方法（参数类型为 Serializable）
            Method method = service.getClass().getMethod("getById", Serializable.class);
            // 调用方法并返回结果
            return method.invoke(service, id);
        } catch (Exception e) {
            // 任何异常均返回 null，不影响主流程
            return null;
        }
    }

    /**
     * 解析 SpEL 表达式，从方法参数中提取动态值（如业务 ID）
     * 
     * @param joinPoint 连接点
     * @param spel SpEL 表达式字符串（如 "#userId" 或 "#user.id"）
     * @return 解析后的字符串值，若解析失败则返回 "N/A"
     */
    private String parseSpel(ProceedingJoinPoint joinPoint, String spel) {
        // 若表达式为空，直接返回默认值
        if (spel.isEmpty()) return "N/A";
        try {
            // 获取方法签名信息
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            // 创建 SpEL 评估上下文
            EvaluationContext context = new StandardEvaluationContext();
            // 获取方法参数值
            Object[] args = joinPoint.getArgs();
            // 获取参数名称数组
            String[] paramNames = signature.getParameterNames();
            // 将参数名和参数值绑定到上下文中，供 SpEL 表达式使用
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
            // 解析表达式并返回值，若结果为 null 则返回 "N/A"
            return Objects.requireNonNull(parser.parseExpression(spel).getValue(context)).toString();
        } catch (Exception e) {
            // 解析失败时返回默认值
            return "N/A";
        }
    }
}