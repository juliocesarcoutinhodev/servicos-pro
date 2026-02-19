package com.servicepro.auth.infrastructure.security;

import com.servicepro.auth.domain.gateway.TokenGateway;
import com.servicepro.auth.domain.model.AccessTokenClaims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final TokenGateway tokenGateway;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            resolveAuthentication(request);
        }

        filterChain.doFilter(request, response);
    }

    private void resolveAuthentication(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return;
        }

        String rawToken = authorizationHeader.substring(BEARER_PREFIX.length()).trim();
        if (rawToken.isBlank()) {
            return;
        }

        try {
            if (!tokenGateway.validateToken(rawToken)) {
                return;
            }

            AccessTokenClaims claims = tokenGateway.extractClaims(rawToken);
            AuthenticatedUserPrincipal principal = AuthenticatedUserPrincipal.fromClaims(claims);
            var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + claims.role().name()));

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    principal,
                    rawToken,
                    authorities
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (RuntimeException exception) {
            SecurityContextHolder.clearContext();
        }
    }
}
