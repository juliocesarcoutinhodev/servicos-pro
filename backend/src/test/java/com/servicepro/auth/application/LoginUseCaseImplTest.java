package com.servicepro.auth.application;

import com.servicepro.auth.application.service.refreshtoken.IssuedRefreshToken;
import com.servicepro.auth.application.service.refreshtoken.RefreshTokenService;
import com.servicepro.auth.application.usecase.login.LoginCommand;
import com.servicepro.auth.application.usecase.login.LoginResult;
import com.servicepro.auth.application.usecase.login.LoginUseCase;
import com.servicepro.auth.application.usecase.login.LoginUseCaseImpl;
import com.servicepro.auth.domain.exception.InvalidCredentialsException;
import com.servicepro.auth.domain.gateway.PasswordHasher;
import com.servicepro.auth.domain.gateway.TokenGateway;
import com.servicepro.auth.domain.gateway.UserGateway;
import com.servicepro.auth.domain.model.Role;
import com.servicepro.auth.domain.model.User;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseImplTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private TokenGateway tokenGateway;

    @Mock
    private RefreshTokenService refreshTokenService;

    private LoginUseCase loginUseCase;

    @BeforeEach
    void setUp() {
        loginUseCase = new LoginUseCaseImpl(
                userGateway,
                passwordHasher,
                tokenGateway,
                refreshTokenService
        );
    }

    @Test
    void shouldLoginSuccessfully() {
        UUID userId = UUID.randomUUID();
        User user = User.restore(
                userId,
                "Joao Silva",
                "joao@email.com",
                "+5511999999999",
                "argon-hash",
                Role.CLIENT,
                true,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
        LoginCommand command = new LoginCommand("JOAO@email.com", "SenhaForte123");

        when(userGateway.findByEmail("joao@email.com")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("SenhaForte123", "argon-hash")).thenReturn(true);
        when(tokenGateway.generateAccessToken(user)).thenReturn("access-token");
        when(tokenGateway.accessTokenTtlSeconds()).thenReturn(900L);
        when(refreshTokenService.create(userId))
                .thenReturn(new IssuedRefreshToken("refresh-token", OffsetDateTime.now().plusDays(7)));

        LoginResult result = loginUseCase.execute(command);

        assertThat(result.tokenPair().accessToken()).isEqualTo("access-token");
        assertThat(result.tokenPair().expiresIn()).isEqualTo(900L);
        assertThat(result.refreshToken()).isEqualTo("refresh-token");

        then(userGateway).should().findByEmail("joao@email.com");
        then(passwordHasher).should().matches("SenhaForte123", "argon-hash");
        then(tokenGateway).should().generateAccessToken(user);
        then(refreshTokenService).should().create(userId);
    }

    @Test
    void shouldThrowWhenUserIsNotFound() {
        LoginCommand command = new LoginCommand("desconhecido@email.com", "SenhaForte123");

        when(userGateway.findByEmail("desconhecido@email.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loginUseCase.execute(command))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Credenciais invalidas");

        then(passwordHasher).shouldHaveNoInteractions();
        then(tokenGateway).shouldHaveNoInteractions();
        then(refreshTokenService).shouldHaveNoInteractions();
    }

    @Test
    void shouldThrowWhenPasswordDoesNotMatch() {
        User user = User.restore(
                UUID.randomUUID(),
                "Joao Silva",
                "joao@email.com",
                "+5511999999999",
                "argon-hash",
                Role.CLIENT,
                true,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
        LoginCommand command = new LoginCommand("joao@email.com", "SenhaErrada123");

        when(userGateway.findByEmail("joao@email.com")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("SenhaErrada123", "argon-hash")).thenReturn(false);

        assertThatThrownBy(() -> loginUseCase.execute(command))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Credenciais invalidas");

        then(tokenGateway).shouldHaveNoInteractions();
        then(refreshTokenService).shouldHaveNoInteractions();
    }
}
