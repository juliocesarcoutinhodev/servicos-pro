package com.servicepro.auth.application;

import com.servicepro.auth.application.usecase.signup.SignupCommand;
import com.servicepro.auth.application.usecase.signup.SignupDomainMapper;
import com.servicepro.auth.application.usecase.signup.SignupUseCase;
import com.servicepro.auth.application.usecase.signup.SignupUseCaseImpl;
import com.servicepro.auth.domain.exception.EmailAlreadyExistsException;
import com.servicepro.auth.domain.exception.InvalidSignupPasswordException;
import com.servicepro.auth.domain.exception.InvalidSignupRoleException;
import com.servicepro.auth.domain.gateway.PasswordHasher;
import com.servicepro.auth.domain.gateway.UserGateway;
import com.servicepro.auth.domain.model.Role;
import com.servicepro.auth.domain.model.User;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SignupUseCaseImplTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private PasswordHasher passwordHasher;

    private SignupUseCase signupUseCase;

    @BeforeEach
    void setUp() {
        SignupDomainMapper signupDomainMapper = Mappers.getMapper(SignupDomainMapper.class);
        signupUseCase = new SignupUseCaseImpl(userGateway, passwordHasher, signupDomainMapper);
    }

    @Test
    void shouldSignupSuccessfully() {
        SignupCommand command = new SignupCommand(
                "Joao Silva",
                "JOAO@EMAIL.COM",
                "+5511999999999",
                "SenhaForte123",
                Role.CLIENT
        );

        when(userGateway.existsByEmail("joao@email.com")).thenReturn(false);
        when(passwordHasher.hash("SenhaForte123")).thenReturn("argon-hash");
        when(userGateway.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return User.restore(
                    UUID.randomUUID(),
                    user.getName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getPasswordHash(),
                    user.getRole(),
                    user.isActive(),
                    OffsetDateTime.now(),
                    OffsetDateTime.now()
            );
        });

        User savedUser = signupUseCase.execute(command);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userGateway).save(captor.capture());

        User captured = captor.getValue();
        assertThat(captured.getEmail()).isEqualTo("joao@email.com");
        assertThat(captured.getName()).isEqualTo("Joao Silva");
        assertThat(captured.getPhone()).isEqualTo("+5511999999999");
        assertThat(captured.getRole()).isEqualTo(Role.CLIENT);
        assertThat(captured.isActive()).isTrue();
        assertThat(captured.getPasswordHash()).isEqualTo("argon-hash");
        then(passwordHasher).should().hash("SenhaForte123");

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("joao@email.com");
    }

    @Test
    void shouldNormalizeAndTrimInputOnSignup() {
        SignupCommand command = new SignupCommand(
                "  Joao Silva  ",
                "  JOAO@EMAIL.COM  ",
                "  +5511999999999  ",
                "SenhaForte123",
                Role.CLIENT
        );

        when(userGateway.existsByEmail("joao@email.com")).thenReturn(false);
        when(passwordHasher.hash("SenhaForte123")).thenReturn("argon-hash");
        when(userGateway.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = signupUseCase.execute(command);

        assertThat(savedUser.getName()).isEqualTo("Joao Silva");
        assertThat(savedUser.getEmail()).isEqualTo("joao@email.com");
        assertThat(savedUser.getPhone()).isEqualTo("+5511999999999");
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {
        SignupCommand command = new SignupCommand(
                "Maria",
                "maria@email.com",
                "+5511988888888",
                "SenhaForte123",
                Role.PROVIDER
        );

        when(userGateway.existsByEmail("maria@email.com")).thenReturn(true);

        assertThatThrownBy(() -> signupUseCase.execute(command))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("maria@email.com");
    }

    @Test
    void shouldThrowWhenRoleIsNotAllowedForSignup() {
        SignupCommand command = new SignupCommand(
                "Finance User",
                "finance@email.com",
                "+5511999999999",
                "SenhaForte123",
                Role.FINANCE
        );

        assertThatThrownBy(() -> signupUseCase.execute(command))
                .isInstanceOf(InvalidSignupRoleException.class)
                .hasMessageContaining("Apenas CLIENT e PROVIDER");
    }

    @Test
    void shouldThrowWhenPasswordLengthIsInvalid() {
        SignupCommand command = new SignupCommand(
                "Joao Silva",
                "joao@email.com",
                "+5511999999999",
                "1234567",
                Role.CLIENT
        );

        assertThatThrownBy(() -> signupUseCase.execute(command))
                .isInstanceOf(InvalidSignupPasswordException.class)
                .hasMessageContaining("entre 8 e 72");

        then(userGateway).shouldHaveNoInteractions();
        then(passwordHasher).shouldHaveNoInteractions();
    }
}
