package com.servicepro.auth.application.service.ratelimit;

public record RateLimitStatus(
        int limit,
        long remaining,
        long resetInSeconds
) {
}
