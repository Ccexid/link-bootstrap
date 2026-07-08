package me.link.bootstrap.infrastructure.config;

import me.link.bootstrap.shared.kernel.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LinkCorsAutoConfigurationTest {

    /**
     * 验证 shouldRejectWildcardOriginPatternWhenCredentialsAllowed 场景。
     */
    @Test
    void shouldRejectWildcardOriginPatternWhenCredentialsAllowed() {
        LinkSecurityProperties properties = new LinkSecurityProperties();
        properties.getCors().setAllowedOriginPatterns(List.of("*"));
        properties.getCors().setAllowCredentials(true);

        LinkCorsAutoConfiguration autoConfiguration = new LinkCorsAutoConfiguration(properties);

        assertThatThrownBy(autoConfiguration::linkCorsFilter)
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("link.security.cors.allowed-origin-patterns");
    }

    /**
     * 验证 shouldAllowConfiguredOriginPatternWhenCredentialsAllowed 场景。
     */
    @Test
    void shouldAllowConfiguredOriginPatternWhenCredentialsAllowed() {
        LinkSecurityProperties properties = new LinkSecurityProperties();
        properties.getCors().setAllowedOriginPatterns(List.of("https://admin.example.com"));
        properties.getCors().setAllowCredentials(true);

        LinkCorsAutoConfiguration autoConfiguration = new LinkCorsAutoConfiguration(properties);

        FilterRegistrationBean<CorsFilter> filterRegistrationBean = autoConfiguration.linkCorsFilter();

        assertThat(filterRegistrationBean.getFilter()).isNotNull();
    }
}
