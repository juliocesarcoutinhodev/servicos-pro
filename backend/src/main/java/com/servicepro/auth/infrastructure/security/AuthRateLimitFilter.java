package com.servicepro.auth.infrastructure.security;

import com.servicepro.auth.application.service.ratelimit.AuthRateLimitAction;
import com.servicepro.auth.application.service.ratelimit.AuthRateLimitService;
import com.servicepro.auth.application.service.ratelimit.RateLimitStatus;
import com.servicepro.auth.domain.exception.RateLimitExceededException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class AuthRateLimitFilter extends OncePerRequestFilter {

    public static final String HEADER_RATE_LIMIT_LIMIT = "X-RateLimit-Limit";
    public static final String HEADER_RATE_LIMIT_REMAINING = "X-RateLimit-Remaining";
    public static final String HEADER_RATE_LIMIT_RESET = "X-RateLimit-Reset";

    private final AuthRateLimitService authRateLimitService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return AuthRateLimitAction.fromRequest(request).isEmpty();
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        Optional<AuthRateLimitAction> actionOptional = AuthRateLimitAction.fromRequest(request);
        if (actionOptional.isPresent()) {
            AuthRateLimitAction action = actionOptional.get();
            try {
                RateLimitStatus status = authRateLimitService.consume(action, resolveClientIp(request));
                applyRateLimitHeaders(response, status);
            } catch (RateLimitExceededException exception) {
                handlerExceptionResolver.resolveException(request, response, null, exception);
                return;
            } catch (RuntimeException exception) {
                log.error("Falha ao aplicar rate limit no endpoint de auth. Seguindo sem bloqueio.", exception);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }

        return request.getRemoteAddr();
    }

    private void applyRateLimitHeaders(HttpServletResponse response, RateLimitStatus status) {
        response.setHeader(HEADER_RATE_LIMIT_LIMIT, String.valueOf(status.limit()));
        response.setHeader(HEADER_RATE_LIMIT_REMAINING, String.valueOf(status.remaining()));
        response.setHeader(HEADER_RATE_LIMIT_RESET, String.valueOf(status.resetInSeconds()));
    }
}
