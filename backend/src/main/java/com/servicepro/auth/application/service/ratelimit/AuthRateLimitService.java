package com.servicepro.auth.application.service.ratelimit;

public interface AuthRateLimitService {

    RateLimitStatus consume(AuthRateLimitAction action, String clientIp);
}
