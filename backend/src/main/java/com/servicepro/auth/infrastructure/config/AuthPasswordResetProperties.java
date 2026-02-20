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
@ConfigurationProperties(prefix = "auth.password-reset")
public class AuthPasswordResetProperties {

    @Min(60)
    private long tokenTtlSeconds = 1800;
}
