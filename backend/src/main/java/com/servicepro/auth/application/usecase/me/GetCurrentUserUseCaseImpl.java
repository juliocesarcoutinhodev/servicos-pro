package com.servicepro.auth.application.usecase.me;

import com.servicepro.auth.domain.exception.InvalidCredentialsException;
import com.servicepro.auth.domain.gateway.UserGateway;
import com.servicepro.auth.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetCurrentUserUseCaseImpl implements GetCurrentUserUseCase {

    private final UserGateway userGateway;

    @Override
    @Transactional(readOnly = true)
    public User execute(GetCurrentUserCommand command) {
        User user = userGateway.findById(command.userId())
                .orElseThrow(InvalidCredentialsException::new);

        if (!user.isActive()) {
            throw new InvalidCredentialsException();
        }

        return user;
    }
}
