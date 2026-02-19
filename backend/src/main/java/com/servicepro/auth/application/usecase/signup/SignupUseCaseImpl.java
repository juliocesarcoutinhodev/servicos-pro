package com.servicepro.auth.application.usecase.signup;

import com.servicepro.auth.domain.exception.EmailAlreadyExistsException;
import com.servicepro.auth.domain.gateway.PasswordHasher;
import com.servicepro.auth.domain.gateway.UserGateway;
import com.servicepro.auth.domain.model.SignupData;
import com.servicepro.auth.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignupUseCaseImpl implements SignupUseCase {

    private final UserGateway userGateway;
    private final PasswordHasher passwordHasher;
    private final SignupDomainMapper signupDomainMapper;

    @Override
    @Transactional
    public User execute(SignupCommand command) {
        SignupData signupData = signupDomainMapper.toSignupData(command);

        String email = signupData.email().value();
        if (userGateway.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        String encodedPassword = passwordHasher.hash(signupData.password());
        User user = User.signUp(
                signupData.name(),
                signupData.email(),
                signupData.phone(),
                encodedPassword,
                signupData.role()
        );

        return userGateway.save(user);
    }
}
