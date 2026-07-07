package me.link.bootstrap.shared.kernel.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import me.link.bootstrap.infrastructure.security.LoginUserPrincipal;
import me.link.bootstrap.shared.kernel.constant.SecurityConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

class SecurityHelperTest {

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReadCurrentPrincipalFromSpringSecurityContext() {
        LoginUserPrincipal principal = new LoginUserPrincipal(
                1001L,
                2001L,
                2,
                true,
                List.of("system:user:list"),
                List.of(SecurityConstants.ROLE_SUPER_ADMIN)
        );
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                principal,
                "opaque-token-value",
                List.of(new SimpleGrantedAuthority("system:user:list"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThat(SecurityHelper.isLoggedIn()).isTrue();
        assertThat(SecurityHelper.getUserId()).isEqualTo(1001L);
        assertThat(SecurityHelper.getTenantId()).isEqualTo(2001L);
        assertThat(SecurityHelper.isSuperAdmin()).isTrue();
        assertThat(SecurityHelper.getTokenValue()).isEqualTo("opaque-token-value");
    }

    @Test
    void shouldReturnAnonymousDefaultsWhenContextIsEmpty() {
        assertThat(SecurityHelper.isLoggedIn()).isFalse();
        assertThat(SecurityHelper.getUserId()).isNull();
        assertThat(SecurityHelper.getTenantId()).isNull();
        assertThat(SecurityHelper.isSuperAdmin()).isFalse();
        assertThat(SecurityHelper.getTokenValue()).isNull();
    }
}
