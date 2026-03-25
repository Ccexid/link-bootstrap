package me.link.bootstrap.infrastructure.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.link.bootstrap.infrastructure.context.TenantContextHolder;
import me.link.bootstrap.infrastructure.utils.TraceUtils;
import me.link.bootstrap.interfaces.interceptor.TraceInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * 核心安全与上下文配置类
 * 整合了：链路追踪、Sa-Token 认证、多租户同步、自动化清理
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SaTokenSecurityConfig implements WebMvcConfigurer {

    private final TraceInterceptor traceInterceptor;

    /**
     * 白名单路径（无需登录即可访问）
     */
    private static final String[] EXCLUDE_PATHS = {
            "/auth/login",
            "/h2-console/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/druid/**",
            "/favicon.ico",
            "/error"
    };

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info(">>> [Infrastructure] 开始注入全栈拦截器链...");

        // 1. 链路追踪拦截器 (最高优先级: Ordered.HIGHEST_PRECEDENCE)
        // 职责：初始化 MDC traceId，并在 afterCompletion 中清理 traceId
        registry.addInterceptor(traceInterceptor)
                .addPathPatterns("/**")
                .order(Ordered.HIGHEST_PRECEDENCE);

        // 2. Sa-Token & 租户上下文拦截器 (次高优先级: 0)
        // 职责：登录校验、权限校验、TenantId 同步
        registry.addInterceptor(new SaInterceptor(handler -> {
                    // A. 登录校验
                    SaRouter.match("/**")
                            .notMatch(EXCLUDE_PATHS)
                            .check(r -> StpUtil.checkLogin());

                    // B. 租户上下文同步 (核心结合点)
                    // 只有在登录成功后，才从 Sa-Session 同步租户 ID 到本地线程
                    if (StpUtil.isLogin()) {
                        TenantContextHolder.syncFromSaToken();
                    }

                    // C. 角色校验预留 (示例)
                    SaRouter.match("/admin/**", r -> StpUtil.checkRole("admin"));

                }))
                .addPathPatterns("/**")
                .excludePathPatterns(EXCLUDE_PATHS)
                .order(0);

        // 3. 上下文清理拦截器 (最低优先级: Ordered.LOWEST_PRECEDENCE)
        // 职责：兜底清理 TenantContextHolder，防止线程池复用导致的租户数据污染
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public void afterCompletion(@NonNull HttpServletRequest request,
                                        @NonNull HttpServletResponse response,
                                        @NonNull Object handler, Exception ex) {
                // 请求结束，彻底销毁租户信息
                TenantContextHolder.clear();
                TraceUtils.clear();
                // 链路追踪清理已在 TraceInterceptor 内部完成，此处作为双保险也可调用 TraceUtils.clear()
            }
        }).addPathPatterns("/**").order(Ordered.LOWEST_PRECEDENCE);

        log.info(">>> [Infrastructure] 拦截器链配置完成。已排除路径: {}", Arrays.toString(EXCLUDE_PATHS));
    }
}