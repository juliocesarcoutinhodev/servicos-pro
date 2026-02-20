package com.servicepro.auth.application.usecase.signup;

import com.servicepro.auth.domain.exception.EmailAlreadyExistsException;
import com.servicepro.auth.domain.gateway.AccountNotificationGateway;
import com.servicepro.auth.domain.gateway.PasswordHasher;
import com.servicepro.auth.domain.gateway.UserGateway;
import com.servicepro.auth.domain.model.SignupData;
import com.servicepro.auth.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class SignupUseCaseImpl implements SignupUseCase {

    private final UserGateway userGateway;
    private final PasswordHasher passwordHasher;
    private final SignupDomainMapper signupDomainMapper;
    private final AccountNotificationGateway accountNotificationGateway;

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

        User savedUser = userGateway.save(user);
        sendWelcomeEmailAfterCommit(savedUser);
        return savedUser;
    }

    private void sendWelcomeEmailAfterCommit(User user) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            accountNotificationGateway.sendWelcomeEmail(user);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                accountNotificationGateway.sendWelcomeEmail(user);
            }
        });
    }
}
