package com.servicepro.auth.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.servicepro.auth.application.usecase.signup.SignupCommand;
import com.servicepro.auth.application.usecase.signup.SignupUseCase;
import com.servicepro.auth.domain.exception.EmailAlreadyExistsException;
import com.servicepro.auth.domain.model.Role;
import com.servicepro.auth.domain.model.User;
import com.servicepro.auth.infrastructure.config.SecurityConfig;
import com.servicepro.auth.interfaces.dto.SignupRequest;
import com.servicepro.auth.interfaces.dto.UserResponse;
import com.servicepro.auth.interfaces.mapper.SignupRequestMapper;
import com.servicepro.auth.interfaces.mapper.UserMapper;
import com.servicepro.shared.infrastructure.security.RestAccessDeniedHandler;
import com.servicepro.shared.infrastructure.security.RestAuthenticationEntryPoint;
import com.servicepro.shared.interfaces.exception.GenericExceptionHandler;
import com.servicepro.shared.interfaces.exception.NegocioExceptionHandler;
import com.servicepro.shared.interfaces.exception.ValidacaoExceptionHandler;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@Import({
        SecurityConfig.class,
        RestAuthenticationEntryPoint.class,
        RestAccessDeniedHandler.class,
        NegocioExceptionHandler.class,
        ValidacaoExceptionHandler.class,
        GenericExceptionHandler.class
})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SignupUseCase signupUseCase;

    @MockBean
    private SignupRequestMapper signupRequestMapper;

    @MockBean
    private UserMapper userMapper;

    @Test
    void shouldReturn201WhenSignupSucceeds() throws Exception {
        SignupRequest request = new SignupRequest(
                "Joao Silva",
                "joao@email.com",
                "+5511999999999",
                "SenhaForte123",
                Role.CLIENT
        );

        SignupCommand command = new SignupCommand(
                "Joao Silva",
                "joao@email.com",
                "+5511999999999",
                "SenhaForte123",
                Role.CLIENT
        );

        User domainUser = User.restore(
                UUID.randomUUID(),
                "Joao Silva",
                "joao@email.com",
                "+5511999999999",
                "hashed-password",
                Role.CLIENT,
                true,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        UserResponse response = new UserResponse(
                domainUser.getId(),
                domainUser.getName(),
                domainUser.getEmail(),
                domainUser.getPhone(),
                domainUser.getRole(),
                domainUser.getCreatedAt(),
                domainUser.isActive()
        );

        given(signupRequestMapper.toCommand(any(SignupRequest.class))).willReturn(command);
        given(signupUseCase.execute(any(SignupCommand.class))).willReturn(domainUser);
        given(userMapper.toUserResponse(any(User.class))).willReturn(response);

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Usuario cadastrado com sucesso."))
                .andExpect(jsonPath("$.data.email").value("joao@email.com"))
                .andExpect(jsonPath("$.data.role").value("CLIENT"));

        then(signupRequestMapper).should().toCommand(any(SignupRequest.class));
        then(signupUseCase).should().execute(any(SignupCommand.class));
        then(userMapper).should().toUserResponse(any(User.class));
    }

    @Test
    void shouldReturn400WhenPhoneIsInvalid() throws Exception {
        String requestBody = """
                {
                  "name": "Joao Silva",
                  "email": "joao@email.com",
                  "phone": "11999999999",
                  "password": "SenhaForte123",
                  "role": "CLIENT"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("E.164")));

        then(signupUseCase).shouldHaveNoInteractions();
        then(signupRequestMapper).shouldHaveNoInteractions();
    }

    @Test
    void shouldReturn409WhenEmailAlreadyExists() throws Exception {
        SignupRequest request = new SignupRequest(
                "Maria",
                "maria@email.com",
                "+5511988888888",
                "SenhaForte123",
                Role.PROVIDER
        );

        SignupCommand command = new SignupCommand(
                "Maria",
                "maria@email.com",
                "+5511988888888",
                "SenhaForte123",
                Role.PROVIDER
        );

        given(signupRequestMapper.toCommand(any(SignupRequest.class))).willReturn(command);
        given(signupUseCase.execute(any(SignupCommand.class)))
                .willThrow(new EmailAlreadyExistsException("maria@email.com"));

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message", containsString("maria@email.com")));
    }
}
