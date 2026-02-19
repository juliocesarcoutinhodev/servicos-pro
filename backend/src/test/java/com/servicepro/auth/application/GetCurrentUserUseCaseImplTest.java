package com.servicepro.auth.application;

import com.servicepro.auth.application.usecase.me.GetCurrentUserCommand;
import com.servicepro.auth.application.usecase.me.GetCurrentUserUseCase;
import com.servicepro.auth.application.usecase.me.GetCurrentUserUseCaseImpl;
import com.servicepro.auth.domain.exception.InvalidCredentialsException;
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
class GetCurrentUserUseCaseImplTest {

    @Mock
    private UserGateway userGateway;

    private GetCurrentUserUseCase getCurrentUserUseCase;

    @BeforeEach
    void setUp() {
        getCurrentUserUseCase = new GetCurrentUserUseCaseImpl(userGateway);
    }

    @Test
    void shouldReturnCurrentUserWhenUserExistsAndIsActive() {
        UUID userId = UUID.randomUUID();
        User user = User.restore(
                userId,
                "Julio",
                "julio@email.com",
                "+5511999999999",
                "argon-hash",
                Role.CLIENT,
                true,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        when(userGateway.findById(userId)).thenReturn(Optional.of(user));

        User result = getCurrentUserUseCase.execute(new GetCurrentUserCommand(userId));

        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getEmail()).isEqualTo("julio@email.com");
        then(userGateway).should().findById(userId);
    }

    @Test
    void shouldThrowWhenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();
        when(userGateway.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getCurrentUserUseCase.execute(new GetCurrentUserCommand(userId)))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Credenciais invalidas");
    }

    @Test
    void shouldThrowWhenUserIsInactive() {
        UUID userId = UUID.randomUUID();
        User inactiveUser = User.restore(
                userId,
                "Julio",
                "julio@email.com",
                "+5511999999999",
                "argon-hash",
                Role.CLIENT,
                false,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        when(userGateway.findById(userId)).thenReturn(Optional.of(inactiveUser));

        assertThatThrownBy(() -> getCurrentUserUseCase.execute(new GetCurrentUserCommand(userId)))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Credenciais invalidas");
    }
}
