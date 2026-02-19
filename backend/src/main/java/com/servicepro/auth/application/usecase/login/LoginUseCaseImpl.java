package com.servicepro.auth.application.usecase.login;

import com.servicepro.auth.application.service.refreshtoken.IssuedRefreshToken;
import com.servicepro.auth.application.service.refreshtoken.RefreshTokenService;
import com.servicepro.auth.domain.exception.InvalidCredentialsException;
import com.servicepro.auth.domain.gateway.PasswordHasher;
import com.servicepro.auth.domain.gateway.TokenGateway;
import com.servicepro.auth.domain.gateway.UserGateway;
import com.servicepro.auth.domain.model.User;
import com.servicepro.auth.domain.model.valueobject.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginUseCaseImpl implements LoginUseCase {

    private final UserGateway userGateway;
    private final PasswordHasher passwordHasher;
    private final TokenGateway tokenGateway;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public LoginResult execute(LoginCommand command) {
        Email email = Email.of(command.email());

        User user = userGateway.findByEmail(email.value())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordHasher.matches(command.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String accessToken = tokenGateway.generateAccessToken(user);
        IssuedRefreshToken refreshToken = refreshTokenService.create(user.getId());

        TokenPair tokenPair = new TokenPair(accessToken, tokenGateway.accessTokenTtlSeconds());
        return new LoginResult(tokenPair, refreshToken.token());
    }
}
