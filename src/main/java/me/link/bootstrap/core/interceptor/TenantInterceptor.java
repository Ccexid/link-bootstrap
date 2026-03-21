package me.link.bootstrap.core.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.link.bootstrap.core.tenant.TenantContextHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 多租户拦截器
 * 用于在请求处理前解析租户 ID，并在请求结束后清理上下文，防止内存泄漏。
 */
@Component
public class TenantInterceptor implements HandlerInterceptor {

    /**
     * 在业务处理器（即 Controller）执行之前调用。
     * 主要作用：从 HTTP 请求头中提取租户标识（X-Tenant-Id），并将其存入线程本地变量中，
     * 以便后续业务逻辑可以获取当前请求所属的租户。
     *
     * @param request  当前 HTTP 请求对象
     * @param response 当前 HTTP 响应对象
     * @param handler  即将执行的处理器对象
     * @return true 表示继续执行后续拦截器和控制器；false 表示中断请求
     */
    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        // 步骤 1：按照约定，从 HTTP 请求头中获取租户 ID
        String tenantId = request.getHeader("X-Tenant-Id");

        // 步骤 2： 实际开发中可结合认证框架（如 Sa-Token）从会话中获取租户 ID
        // 示例逻辑：如果用户已登录，则优先从会话中获取租户信息
        if (StpUtil.isLogin()) {
            tenantId = (String) StpUtil.getSession().get("tenantId");
        }

        // 步骤 3：如果成功获取到租户 ID，则将其设置到线程上下文中
        if (tenantId != null) {
            TenantContextHolder.setTenantId(tenantId);
        }

        // 步骤 4：返回 true，允许请求继续向下传递
        return true;
    }

    /**
     * 在整个请求结束之后调用（即在视图渲染完成后）。
     * 主要作用：清理当前线程绑定的租户信息。
     * 由于使用了 ThreadLocal 存储租户信息，必须在请求结束后显式清除，
     * 否则在线程复用的场景下（如 Tomcat 线程池）会导致数据污染或内存泄漏。
     *
     * @param request  当前 HTTP 请求对象
     * @param response 当前 HTTP 响应对象
     * @param handler  已执行完成的处理器对象
     * @param ex       请求处理过程中抛出的异常（如果有）
     */
    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        // 步骤 5：请求结束，务必清理 ThreadLocal 中的租户信息，避免内存泄漏和线程复用导致的数据错乱
        TenantContextHolder.clear();
    }
}