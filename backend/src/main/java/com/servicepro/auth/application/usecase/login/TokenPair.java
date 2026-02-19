package com.servicepro.auth.application.usecase.login;

public record TokenPair(
        String accessToken,
        long expiresIn
) {
}
