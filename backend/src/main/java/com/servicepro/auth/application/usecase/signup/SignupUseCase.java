package com.servicepro.auth.application.usecase.signup;

import com.servicepro.auth.domain.model.User;

public interface SignupUseCase {

    User execute(SignupCommand command);
}
