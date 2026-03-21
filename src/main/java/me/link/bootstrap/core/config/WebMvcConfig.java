package me.link.bootstrap.core.config;

import me.link.bootstrap.core.interceptor.TenantInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 配置类
 * 用于注册自定义拦截器，实现多租户等横切关注点的处理
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 注入租户拦截器实例，用于处理请求中的租户信息
    private final TenantInterceptor tenantInterceptor;

    /**
     * 构造函数注入租户拦截器
     * @param tenantInterceptor 租户拦截器实例
     */
    public WebMvcConfig(TenantInterceptor tenantInterceptor) {
        this.tenantInterceptor = tenantInterceptor;
    }

    /**
     * 注册拦截器到 Spring MVC 框架
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 将租户拦截器注册到拦截器链中
        // addPathPatterns("/**") 表示拦截所有路径的请求
        registry.addInterceptor(tenantInterceptor).addPathPatterns("/**");
    }
}