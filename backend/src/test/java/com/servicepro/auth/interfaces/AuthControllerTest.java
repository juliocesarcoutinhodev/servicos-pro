package com.servicepro.auth.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.servicepro.auth.application.usecase.login.LoginCommand;
import com.servicepro.auth.application.usecase.login.LoginResult;
import com.servicepro.auth.application.usecase.login.LoginUseCase;
import com.servicepro.auth.application.usecase.login.TokenPair;
import com.servicepro.auth.application.usecase.refresh.RefreshCommand;
import com.servicepro.auth.application.usecase.refresh.RefreshResult;
import com.servicepro.auth.application.usecase.refresh.RefreshUseCase;
import com.servicepro.auth.application.usecase.signup.SignupCommand;
import com.servicepro.auth.application.usecase.signup.SignupUseCase;
import com.servicepro.auth.domain.exception.EmailAlreadyExistsException;
import com.servicepro.auth.domain.exception.InvalidCredentialsException;
import com.servicepro.auth.domain.exception.TokenRevokedException;
import com.servicepro.auth.domain.model.Role;
import com.servicepro.auth.domain.model.User;
import com.servicepro.auth.infrastructure.config.SecurityConfig;
import com.servicepro.auth.infrastructure.security.CookieUtils;
import com.servicepro.auth.interfaces.dto.LoginRequest;
import com.servicepro.auth.interfaces.dto.SignupRequest;
import com.servicepro.auth.interfaces.dto.UserResponse;
import com.servicepro.auth.interfaces.mapper.LoginRequestMapper;
import com.servicepro.auth.interfaces.mapper.SignupRequestMapper;
import com.servicepro.auth.interfaces.mapper.UserMapper;
import com.servicepro.shared.infrastructure.security.RestAccessDeniedHandler;
import com.servicepro.shared.infrastructure.security.RestAuthenticationEntryPoint;
import com.servicepro.shared.interfaces.exception.GenericExceptionHandler;
import com.servicepro.shared.interfaces.exception.NegocioExceptionHandler;
import com.servicepro.shared.interfaces.exception.ValidacaoExceptionHandler;
import java.time.OffsetDateTime;
import java.util.UUID;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
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
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SignupUseCase signupUseCase;

    @MockBean
    private LoginUseCase loginUseCase;

    @MockBean
    private RefreshUseCase refreshUseCase;

    @MockBean
    private SignupRequestMapper signupRequestMapper;

    @MockBean
    private LoginRequestMapper loginRequestMapper;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private CookieUtils cookieUtils;

    @MockBean
    private UserDetailsService userDetailsService;

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

    @Test
    void shouldReturn200WhenLoginSucceeds() throws Exception {
        LoginRequest request = new LoginRequest("joao@email.com", "SenhaForte123");
        LoginCommand command = new LoginCommand("joao@email.com", "SenhaForte123");
        TokenPair tokenPair = new TokenPair("access-token", 900);
        LoginResult loginResult = new LoginResult(tokenPair, "refresh-token");
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", "refresh-token")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/v1/auth/refresh")
                .maxAge(604800)
                .build();

        given(loginRequestMapper.toCommand(any(LoginRequest.class))).willReturn(command);
        given(loginUseCase.execute(any(LoginCommand.class))).willReturn(loginResult);
        given(cookieUtils.buildRefreshTokenCookie("refresh-token")).willReturn(refreshCookie);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Login realizado com sucesso."))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.expiresIn").value(900))
                .andExpect(jsonPath("$.data.refreshToken").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                        .string(HttpHeaders.SET_COOKIE, containsString("refresh_token=refresh-token")))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                        .string(HttpHeaders.SET_COOKIE, containsString("HttpOnly")))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                        .string(HttpHeaders.SET_COOKIE, containsString("Secure")))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                        .string(HttpHeaders.SET_COOKIE, containsString("SameSite=Strict")));

        then(loginRequestMapper).should().toCommand(any(LoginRequest.class));
        then(loginUseCase).should().execute(any(LoginCommand.class));
        then(cookieUtils).should().buildRefreshTokenCookie("refresh-token");
    }

    @Test
    void shouldReturn401WhenLoginCredentialsAreInvalid() throws Exception {
        LoginRequest request = new LoginRequest("joao@email.com", "SenhaErrada123");
        LoginCommand command = new LoginCommand("joao@email.com", "SenhaErrada123");

        given(loginRequestMapper.toCommand(any(LoginRequest.class))).willReturn(command);
        given(loginUseCase.execute(any(LoginCommand.class))).willThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message", containsString("Credenciais invalidas")));
    }

    @Test
    void shouldReturn200WhenRefreshSucceeds() throws Exception {
        TokenPair tokenPair = new TokenPair("new-access-token", 900L);
        RefreshResult refreshResult = new RefreshResult(tokenPair, "new-refresh-token");
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", "new-refresh-token")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/v1/auth/refresh")
                .maxAge(604800)
                .build();

        given(refreshUseCase.execute(any(RefreshCommand.class))).willReturn(refreshResult);
        given(cookieUtils.buildRefreshTokenCookie("new-refresh-token")).willReturn(refreshCookie);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .cookie(new Cookie("refresh_token", "old-refresh-token")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Token atualizado com sucesso."))
                .andExpect(jsonPath("$.data.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.data.expiresIn").value(900))
                .andExpect(jsonPath("$.data.refreshToken").doesNotExist())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                        .string(HttpHeaders.SET_COOKIE, containsString("refresh_token=new-refresh-token")));

        then(refreshUseCase).should().execute(any(RefreshCommand.class));
        then(cookieUtils).should().buildRefreshTokenCookie("new-refresh-token");
    }

    @Test
    void shouldReturn401WhenRefreshTokenIsRevoked() throws Exception {
        given(refreshUseCase.execute(any(RefreshCommand.class))).willThrow(new TokenRevokedException());

        mockMvc.perform(post("/api/v1/auth/refresh"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message", containsString("Refresh token invalido")));
    }
}
