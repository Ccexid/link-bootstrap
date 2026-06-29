package me.link.bootstrap.infrastructure.config;

import lombok.RequiredArgsConstructor;
import me.link.bootstrap.shared.kernel.constant.GlobalConstants;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 跨域资源共享 (CORS) 自动配置中心
 * <p>
 * 基于底层最高优先级的 Filter 实现跨域，从根源上规避了“鉴权拦截器导致跨域配置失效”及“容器提早初始化导致AOP失效”的两大痛点。
 * </p>
 *
 * @author 7Link
 */
@AutoConfiguration
// 💡 仅当当前环境是一个 Web 应用（Servlet 容器）时，该自动配置才激活，避免命令行或测试任务报错
/**
 * 跨域自动配置，集中定义接口跨域访问策略。
 */
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(CorsFilter.class)
@RequiredArgsConstructor
public class LinkCorsAutoConfiguration {

    private final LinkSecurityProperties linkSecurityProperties;

    @SuppressWarnings("null")
    @Bean
    @ConditionalOnMissingBean(name = "linkCorsFilter")
    public FilterRegistrationBean<CorsFilter> linkCorsFilter() {
        CorsConfiguration config = getCorsConfiguration();

        // 7. 配置拦截路径：仅对你定义的全局 API 前缀生效，精准防控
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(GlobalConstants.API_PREFIX + "/**", config);

        // 8. 包装为 FilterRegistrationBean，并显式设置为过滤链第一优先级
        // 确保跨域响应在进入核心的 Spring MVC 拦截器、Sa-Token 鉴权过滤器之前就直接生效返回
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    private @NonNull CorsConfiguration getCorsConfiguration() {
        LinkSecurityProperties.Cors cors = linkSecurityProperties.getCors();
        List<String> allowedOriginPatterns = normalize(cors.getAllowedOriginPatterns());
        if (cors.isAllowCredentials() && containsGlobalWildcard(allowedOriginPatterns)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,
                    "link.security.cors.allow-credentials=true cannot be used with wildcard origin pattern '*'. "
                            + "Configure link.security.cors.allowed-origin-patterns with explicit domains.");
        }

        CorsConfiguration config = new CorsConfiguration();

        // 1. 允许的源模式。生产环境必须通过配置指定明确域名白名单。
        config.setAllowedOriginPatterns(allowedOriginPatterns);

        // 2. 允许的标准 HTTP 请求方法
        config.setAllowedMethods(normalize(cors.getAllowedMethods()));

        // 3. 允许的请求头
        config.setAllowedHeaders(normalize(cors.getAllowedHeaders()));

        // 4. 显式暴露给前端能够拿到的 Response Header（主要是链路追踪 ID）
        config.setExposedHeaders(normalize(cors.getExposedHeaders()));

        // 5. 是否允许携带 Cookie 等凭证信息
        config.setAllowCredentials(cors.isAllowCredentials());

        // 6. 预检请求（OPTIONS）的缓存时间，单位为秒（1小时内无需重复发送 OPTIONS 探测请求）
        Duration maxAge = cors.getMaxAge();
        config.setMaxAge(maxAge == null ? 3600L : maxAge.getSeconds());
        return config;
    }

    private static List<String> normalize(List<String> values) {
        if (values == null) {
            return List.of();
        }
        return values.stream()
                .filter(Objects::nonNull)
                .flatMap(value -> Arrays.stream(value.split(",")))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .distinct()
                .toList();
    }

    private static boolean containsGlobalWildcard(List<String> allowedOriginPatterns) {
        return allowedOriginPatterns.stream().anyMatch("*"::equals);
    }
}
