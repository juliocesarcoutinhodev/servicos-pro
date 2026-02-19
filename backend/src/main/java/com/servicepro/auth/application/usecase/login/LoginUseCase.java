package com.servicepro.auth.application.usecase.login;

public interface LoginUseCase {

    LoginResult execute(LoginCommand command);
}
