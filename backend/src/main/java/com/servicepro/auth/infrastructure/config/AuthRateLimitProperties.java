package com.servicepro.auth.infrastructure.config;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@Component
@ConfigurationProperties(prefix = "auth.rate-limit")
public class AuthRateLimitProperties {

    @Min(1)
    private int maxRequestsPerWindow = 10;

    @Min(1)
    private long windowSeconds = 60;
}
