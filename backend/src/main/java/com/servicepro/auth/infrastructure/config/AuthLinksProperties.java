package com.servicepro.auth.infrastructure.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@Component
@ConfigurationProperties(prefix = "auth.links")
public class AuthLinksProperties {

    @NotBlank
    private String loginUrl = "http://localhost:19006/login";

    @NotBlank
    private String resetPasswordUrl = "http://localhost:19006/reset-password?token=%s";
}
