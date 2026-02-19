package com.servicepro.auth.application.usecase.me;

import com.servicepro.auth.domain.model.User;

public interface GetCurrentUserUseCase {

    User execute(GetCurrentUserCommand command);
}
