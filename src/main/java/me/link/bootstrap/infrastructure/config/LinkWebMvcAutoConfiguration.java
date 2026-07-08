package me.link.bootstrap.infrastructure.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.link.bootstrap.infrastructure.crypto.ApiCryptoProperties;
import me.link.bootstrap.infrastructure.crypto.ApiCryptoService;
import me.link.bootstrap.infrastructure.crypto.MobileCryptoProperties;
import me.link.bootstrap.infrastructure.crypto.MobileCryptoService;
import me.link.bootstrap.interfaces.web.filter.ApiCryptoRequestFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Web MVC 自动配置，注册请求参数蛇形命名到驼峰命名的兼容绑定过滤器。
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(OncePerRequestFilter.class)
@EnableConfigurationProperties({ApiCryptoProperties.class, MobileCryptoProperties.class})
public class LinkWebMvcAutoConfiguration {

    /**
     * 创建 APICryptoService Bean。
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "link.api-crypto", name = "enabled", havingValue = "true")
    public ApiCryptoService apiCryptoService(ApiCryptoProperties properties) {
        return new ApiCryptoService(properties);
    }

    /**
     * 创建 手机号CryptoService Bean。
     */
    @Bean
    @ConditionalOnMissingBean
    public MobileCryptoService mobileCryptoService(MobileCryptoProperties properties) {
        return new MobileCryptoService(properties);
    }

    /**
     * 创建 APICrypto请求Filter Bean。
     */
    @Bean
    @ConditionalOnMissingBean(name = "apiCryptoRequestFilter")
    @ConditionalOnProperty(prefix = "link.api-crypto", name = "enabled", havingValue = "true")
    public FilterRegistrationBean<ApiCryptoRequestFilter> apiCryptoRequestFilter(ApiCryptoService apiCryptoService,
                                                                                 ObjectMapper objectMapper) {
        FilterRegistrationBean<ApiCryptoRequestFilter> bean =
                new FilterRegistrationBean<>(new ApiCryptoRequestFilter(apiCryptoService, objectMapper));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        return bean;
    }

    /**
     * 创建 Snake命名参数BindingFilter Bean。
     */
    @Bean
    @ConditionalOnMissingBean(name = "snakeCaseParameterBindingFilter")
    public FilterRegistrationBean<OncePerRequestFilter> snakeCaseParameterBindingFilter() {
        FilterRegistrationBean<OncePerRequestFilter> bean = new FilterRegistrationBean<>(new SnakeCaseParameterBindingFilter());
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 20);
        return bean;
    }

    private static final class SnakeCaseParameterBindingFilter extends OncePerRequestFilter {

        /**
         * {@inheritDoc}
         */
        @Override
        protected void doFilterInternal(@NonNull HttpServletRequest request,
                                        @NonNull HttpServletResponse response,
                                        @NonNull FilterChain filterChain) throws ServletException, IOException {
            filterChain.doFilter(new SnakeCaseParameterBindingRequest(request), response);
        }
    }

    private static final class SnakeCaseParameterBindingRequest extends HttpServletRequestWrapper {

        private Map<String, String[]> parameterMap;

        /**
         * 创建Snake命名参数Binding请求实例。
         */
        private SnakeCaseParameterBindingRequest(HttpServletRequest request) {
            super(request);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getParameter(String name) {
            String[] values = getParameterValues(name);
            return values == null || values.length == 0 ? null : values[0];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Map<String, String[]> getParameterMap() {
            if (parameterMap == null) {
                parameterMap = buildParameterMap(super.getParameterMap());
            }
            return parameterMap;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Enumeration<String> getParameterNames() {
            return Collections.enumeration(getParameterMap().keySet());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String[] getParameterValues(String name) {
            return getParameterMap().get(name);
        }

        /**
         * 构建参数Map。
         */
        private static Map<String, String[]> buildParameterMap(Map<String, String[]> source) {
            Map<String, String[]> target = new LinkedHashMap<>(source);
            source.forEach((name, values) -> target.putIfAbsent(toCamelCase(name), values));
            return Collections.unmodifiableMap(target);
        }

        /**
         * 转换为驼峰命名。
         */
        private static String toCamelCase(String name) {
            if (name == null || name.indexOf('_') < 0) {
                return name;
            }

            StringBuilder builder = new StringBuilder(name.length());
            boolean upperCaseNext = false;
            for (int i = 0; i < name.length(); i++) {
                char current = name.charAt(i);
                if (current == '_') {
                    upperCaseNext = !builder.isEmpty();
                    continue;
                }
                if (upperCaseNext) {
                    builder.append(Character.toUpperCase(current));
                    upperCaseNext = false;
                } else {
                    builder.append(current);
                }
            }
            return builder.toString();
        }
    }
}
