package me.link.bootstrap.shared.kernel.database.mybatis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记方法或类内部执行的 SQL 跳过 MyBatis-Plus 多租户过滤。
 * <p>
 * 由 {@link TenantIgnoreAspect} 通过 AOP 自动管理 {@link TenantContextHolder} 的生命周期：
 * 进入注解作用域时设置忽略标记，退出时清理。
 * </p>
 * <p>
 * <b>典型场景</b>：
 * <ul>
 *   <li>登录认证：按 username 查 user 时尚未知道 tenant_id；</li>
 *   <li>超管运维：跨租户管理工单、统计报表等；</li>
 *   <li>系统级定时任务：归档、数据修复等无登录态调度。</li>
 * </ul>
 * </p>
 * <p>
 * <b>注意</b>：使用本注解意味着主动放弃数据隔离，请确保接口层另有访问控制（如超管角色校验）。
 * </p>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface TenantIgnore {
}
