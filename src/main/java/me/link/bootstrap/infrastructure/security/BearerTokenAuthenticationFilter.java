package me.link.bootstrap.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.infrastructure.config.LinkSecurityProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class BearerTokenAuthenticationFilter extends OncePerRequestFilter {

    private final SecurityTokenSessionService securityTokenSessionService;
    private final LinkSecurityProperties properties;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = resolveToken(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        securityTokenSessionService.load(token).ifPresent(session -> {
            LoginUserPrincipal principal = securityTokenSessionService.toPrincipal(session);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    principal,
                    token,
                    authorities(session)
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        });
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(properties.getToken().getTokenName());
        if (header == null || header.isBlank()) {
            return null;
        }
        String prefix = properties.getToken().getTokenPrefix();
        String expectedPrefix = prefix + " ";
        if (!header.regionMatches(true, 0, expectedPrefix, 0, expectedPrefix.length())) {
            return null;
        }
        String token = header.substring(expectedPrefix.length()).trim();
        return token.isEmpty() ? null : token;
    }

    private List<GrantedAuthority> authorities(SecurityTokenSession session) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (session.getPermissions() != null) {
            session.getPermissions().forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission)));
        }
        if (session.getRoles() != null) {
            session.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
        }
        return authorities;
    }
}
