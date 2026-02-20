package com.servicepro.auth.application.service.ratelimit;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

public enum AuthRateLimitAction {
    LOGIN("login", "/api/v1/auth/login"),
    SIGNUP("signup", "/api/v1/auth/signup"),
    FORGOT_PASSWORD("forgot-password", "/api/v1/auth/forgot-password"),
    RESET_PASSWORD("reset-password", "/api/v1/auth/reset-password");

    private final String keySegment;
    private final String path;

    AuthRateLimitAction(String keySegment, String path) {
        this.keySegment = keySegment;
        this.path = path;
    }

    public String keySegment() {
        return keySegment;
    }

    public static Optional<AuthRateLimitAction> fromRequest(HttpServletRequest request) {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return Optional.empty();
        }

        String requestPath = request.getRequestURI();
        for (AuthRateLimitAction action : values()) {
            if (action.path.equals(requestPath)) {
                return Optional.of(action);
            }
        }
        return Optional.empty();
    }
}
