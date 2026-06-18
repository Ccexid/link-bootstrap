package me.link.bootstrap.infrastructure.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import me.link.bootstrap.shared.kernel.constant.GlobalConstants;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Sa-Token 认证拦截器配置。
 * <p>
 * 注册全局登录态拦截器:对所有路径默认要求登录态,在白名单(登录、Swagger、Actuator)中显式放行。
 * 细粒度权限校验由 Sa-Token 提供的 {@code @SaCheckPermission} / {@code @SaCheckRole} AOP 注解处理,
 * 在 Controller 或 Service 方法上声明,无需在此手动配置路径级权限。
 * </p>
 * <p>
 * 鉴权失败时由 Sa-Token 抛出 {@code NotLoginException} / {@code NotPermissionException},
 * 已在 {@code GlobalExceptionHandler} 中统一映射为 401 / 403 业务响应。
 * </p>
 */
@Slf4j
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {

    /**
     * 白名单:未登录即可访问。
     * <ul>
     *   <li>{@code /api/v1/auth/login} - 登录入口本身必须放行,否则前端无法首次取得 Token</li>
     *   <li>{@code /api/v1/auth/mobile-login} - 手机验证码登录入口必须放行</li>
     *   <li>{@code /api/v1/auth/mobile-code} - 发送手机验证码入口必须放行</li>
     *   <li>{@code /actuator/**} - 健康检查与监控端点(prod 已限制只暴露 health/info)</li>
     *   <li>{@code /error} - Spring Boot 默认错误页转发路径</li>
     *   <li>Swagger/SpringDoc - dev/test 环境调试,prod 已通过 enabled=false 关闭暴露</li>
     * </ul>
     */
    private static final List<String> WHITELIST = List.of(
            GlobalConstants.API_PREFIX + "/auth/login",
            GlobalConstants.API_PREFIX + "/auth/mobile-login",
            GlobalConstants.API_PREFIX + "/auth/mobile-code",
            "/actuator/**",
            "/error",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/favicon.ico"
    );

    @SuppressWarnings("null")
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handler -> StpUtil.checkLogin()))
                .addPathPatterns("/**")
                .excludePathPatterns(WHITELIST);
        log.info("[Sa-Token] 全局登录态拦截器已注册,白名单条目:{}", WHITELIST.size());
    }
}
