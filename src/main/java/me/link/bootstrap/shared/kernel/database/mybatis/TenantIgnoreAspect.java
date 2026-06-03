package me.link.bootstrap.shared.kernel.database.mybatis;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * {@link TenantIgnore @TenantIgnore} 切面，自动管理 {@link TenantContextHolder} 生命周期。
 * <p>
 * 进入注解作用域时调用 {@link TenantContextHolder#ignore()}，退出时按"嵌套保留外层语义"
 * 原则清理：仅当本次调用是最外层启用者（即进入前并未处于忽略模式）时才执行
 * {@link TenantContextHolder#clear()}，避免内层方法提前清理污染外层。
 * </p>
 * <p>
 * Order 设为最高优先级，确保比业务切面（如 {@code @Transactional}、{@code OperateLogAspect}）
 * 更外层，使其内部所有 SQL 都在忽略模式下执行。
 * </p>
 */
@Aspect
@Component
@Order(Integer.MIN_VALUE)
public class TenantIgnoreAspect {

    @Around("@annotation(me.link.bootstrap.shared.kernel.database.mybatis.TenantIgnore) "
            + "|| @within(me.link.bootstrap.shared.kernel.database.mybatis.TenantIgnore)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        boolean alreadyIgnored = TenantContextHolder.isIgnore();
        if (!alreadyIgnored) {
            TenantContextHolder.ignore();
        }
        try {
            return joinPoint.proceed();
        } finally {
            if (!alreadyIgnored) {
                TenantContextHolder.clear();
            }
        }
    }
}
