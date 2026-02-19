package com.servicepro.auth.application.usecase.login;

public record LoginCommand(
        String email,
        String password
) {
}
