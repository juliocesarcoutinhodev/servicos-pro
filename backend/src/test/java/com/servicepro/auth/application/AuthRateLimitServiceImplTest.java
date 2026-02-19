package com.servicepro.auth.application;

import com.servicepro.auth.application.service.ratelimit.AuthRateLimitAction;
import com.servicepro.auth.application.service.ratelimit.AuthRateLimitService;
import com.servicepro.auth.application.service.ratelimit.AuthRateLimitServiceImpl;
import com.servicepro.auth.application.service.ratelimit.RateLimitStatus;
import com.servicepro.auth.domain.exception.RateLimitExceededException;
import com.servicepro.auth.domain.gateway.RateLimitGateway;
import com.servicepro.auth.infrastructure.config.AuthRateLimitProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthRateLimitServiceImplTest {

    @Mock
    private RateLimitGateway rateLimitGateway;

    private AuthRateLimitProperties properties;
    private AuthRateLimitService authRateLimitService;

    @BeforeEach
    void setUp() {
        properties = new AuthRateLimitProperties();
        properties.setMaxRequestsPerWindow(10);
        properties.setWindowSeconds(60);
        authRateLimitService = new AuthRateLimitServiceImpl(rateLimitGateway, properties);
    }

    @Test
    void shouldAllowRequestAndReturnCounterWhenWithinLimit() {
        when(rateLimitGateway.incrementAndGet("rate_limit:login:127.0.0.1", java.time.Duration.ofSeconds(60)))
                .thenReturn(3L);
        when(rateLimitGateway.ttlSeconds("rate_limit:login:127.0.0.1"))
                .thenReturn(51L);

        RateLimitStatus status = authRateLimitService.consume(AuthRateLimitAction.LOGIN, "127.0.0.1");

        assertThat(status.limit()).isEqualTo(10);
        assertThat(status.remaining()).isEqualTo(7);
        assertThat(status.resetInSeconds()).isEqualTo(51);
    }

    @Test
    void shouldThrowWhenRateLimitIsExceeded() {
        when(rateLimitGateway.incrementAndGet("rate_limit:signup:127.0.0.1", java.time.Duration.ofSeconds(60)))
                .thenReturn(11L);
        when(rateLimitGateway.ttlSeconds("rate_limit:signup:127.0.0.1"))
                .thenReturn(42L);

        assertThatThrownBy(() -> authRateLimitService.consume(AuthRateLimitAction.SIGNUP, "127.0.0.1"))
                .isInstanceOf(RateLimitExceededException.class)
                .hasMessageContaining("42 segundos");
    }
}
