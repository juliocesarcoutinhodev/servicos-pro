package com.servicepro.auth.application.usecase.login;

public record LoginResult(
        TokenPair tokenPair,
        String refreshToken
) {
}
