package com.servicepro.auth.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.servicepro.auth.application.service.ratelimit.AuthRateLimitAction;
import com.servicepro.auth.application.service.ratelimit.AuthRateLimitService;
import com.servicepro.auth.application.service.ratelimit.RateLimitStatus;
import com.servicepro.auth.application.usecase.forgotpassword.ForgotPasswordCommand;
import com.servicepro.auth.application.usecase.forgotpassword.ForgotPasswordUseCase;
import com.servicepro.auth.application.usecase.login.LoginCommand;
import com.servicepro.auth.application.usecase.login.LoginResult;
import com.servicepro.auth.application.usecase.login.LoginUseCase;
import com.servicepro.auth.application.usecase.login.TokenPair;
import com.servicepro.auth.application.usecase.logout.LogoutCommand;
import com.servicepro.auth.application.usecase.logout.LogoutUseCase;
import com.servicepro.auth.application.usecase.me.GetCurrentUserCommand;
import com.servicepro.auth.application.usecase.me.GetCurrentUserUseCase;
import com.servicepro.auth.application.usecase.refresh.RefreshCommand;
import com.servicepro.auth.application.usecase.refresh.RefreshResult;
import com.servicepro.auth.application.usecase.refresh.RefreshUseCase;
import com.servicepro.auth.application.usecase.resetpassword.ResetPasswordCommand;
import com.servicepro.auth.application.usecase.resetpassword.ResetPasswordUseCase;
import com.servicepro.auth.application.usecase.signup.SignupCommand;
import com.servicepro.auth.application.usecase.signup.SignupUseCase;
import com.servicepro.auth.domain.exception.EmailAlreadyExistsException;
import com.servicepro.auth.domain.exception.InvalidCredentialsException;
import com.servicepro.auth.domain.exception.InvalidPasswordResetTokenException;
import com.servicepro.auth.domain.exception.RateLimitExceededException;
import com.servicepro.auth.domain.exception.TokenRevokedException;
import com.servicepro.auth.domain.gateway.TokenGateway;
import com.servicepro.auth.domain.model.AccessTokenClaims;
import com.servicepro.auth.domain.model.Role;
import com.servicepro.auth.domain.model.User;
import com.servicepro.auth.infrastructure.config.SecurityConfig;
import com.servicepro.auth.infrastructure.security.AuthRateLimitFilter;
import com.servicepro.auth.infrastructure.security.CookieUtils;
import com.servicepro.auth.interfaces.dto.ForgotPasswordRequest;
import com.servicepro.auth.interfaces.dto.LoginRequest;
import com.servicepro.auth.interfaces.dto.ResetPasswordRequest;
import com.servicepro.auth.interfaces.dto.SignupRequest;
import com.servicepro.auth.interfaces.dto.UserResponse;
import com.servicepro.auth.interfaces.mapper.ForgotPasswordRequestMapper;
import com.servicepro.auth.interfaces.mapper.LoginRequestMapper;
import com.servicepro.auth.interfaces.mapper.ResetPasswordRequestMapper;
import com.servicepro.auth.interfaces.mapper.SignupRequestMapper;
import com.servicepro.auth.interfaces.mapper.UserMapper;
import com.servicepro.shared.infrastructure.security.RestAccessDeniedHandler;
import com.servicepro.shared.infrastructure.security.RestAuthenticationEntryPoint;
import com.servicepro.shared.interfaces.exception.GenericExceptionHandler;
import com.servicepro.shared.interfaces.exception.NegocioExceptionHandler;
import com.servicepro.shared.interfaces.exception.ValidacaoExceptionHandler;
import jakarta.servlet.http.Cookie;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    private static final String CLIENT_TOKEN = "client-token";
    private static final String ADMIN_TOKEN = "admin-token";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SignupUseCase signupUseCase;

    @MockBean
    private LoginUseCase loginUseCase;

    @MockBean
    private ForgotPasswordUseCase forgotPasswordUseCase;

    @MockBean
    private ResetPasswordUseCase resetPasswordUseCase;

    @MockBean
    private RefreshUseCase refreshUseCase;

    @MockBean
    private GetCurrentUserUseCase getCurrentUserUseCase;

    @MockBean
    private LogoutUseCase logoutUseCase;

    @MockBean
    private AuthRateLimitService authRateLimitService;

    @MockBean
    private SignupRequestMapper signupRequestMapper;

    @MockBean
    private LoginRequestMapper loginRequestMapper;

    @MockBean
    private ForgotPasswordRequestMapper forgotPasswordRequestMapper;

    @MockBean
    private ResetPasswordRequestMapper resetPasswordRequestMapper;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private TokenGateway tokenGateway;

    @MockBean
    private CookieUtils cookieUtils;

    @MockBean
    private UserDetailsService userDetailsService;

    @BeforeEach
    void setupRateLimitDefaults() {
        given(authRateLimitService.consume(any(AuthRateLimitAction.class), anyString()))
                .willReturn(new RateLimitStatus(10, 9, 60));
    }

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

        UserResponse response = toUserResponse(domainUser);

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
                        .string(HttpHeaders.SET_COOKIE, containsString("SameSite=Strict")))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                        .string(AuthRateLimitFilter.HEADER_RATE_LIMIT_LIMIT, "10"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                        .string(AuthRateLimitFilter.HEADER_RATE_LIMIT_REMAINING, "9"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                        .string(AuthRateLimitFilter.HEADER_RATE_LIMIT_RESET, "60"));

        then(loginRequestMapper).should().toCommand(any(LoginRequest.class));
        then(loginUseCase).should().execute(any(LoginCommand.class));
        then(cookieUtils).should().buildRefreshTokenCookie("refresh-token");
    }

    @Test
    void shouldReturn429WhenLoginRateLimitIsExceeded() throws Exception {
        given(authRateLimitService.consume(eq(AuthRateLimitAction.LOGIN), anyString()))
                .willThrow(new RateLimitExceededException("login", 10, 0, 42, 60));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"joao@email.com\",\"password\":\"SenhaForte123\"}"))
                .andExpect(status().isTooManyRequests())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                        .string(HttpHeaders.RETRY_AFTER, "42"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                        .string(AuthRateLimitFilter.HEADER_RATE_LIMIT_LIMIT, "10"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                        .string(AuthRateLimitFilter.HEADER_RATE_LIMIT_REMAINING, "0"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                        .string(AuthRateLimitFilter.HEADER_RATE_LIMIT_RESET, "42"))
                .andExpect(jsonPath("$.status").value(429))
                .andExpect(jsonPath("$.message", containsString("Limite de requisicoes")))
                .andExpect(jsonPath("$.details.action").value("login"))
                .andExpect(jsonPath("$.details.limit").value(10))
                .andExpect(jsonPath("$.details.remaining").value(0))
                .andExpect(jsonPath("$.details.retryAfterSeconds").value(42));
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
    void shouldReturn202WhenForgotPasswordIsRequested() throws Exception {
        ForgotPasswordRequest request = new ForgotPasswordRequest("joao@email.com");
        ForgotPasswordCommand command = new ForgotPasswordCommand("joao@email.com");

        given(forgotPasswordRequestMapper.toCommand(any(ForgotPasswordRequest.class))).willReturn(command);

        mockMvc.perform(post("/api/v1/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value(202))
                .andExpect(jsonPath("$.message")
                        .value("Se o email estiver cadastrado, enviaremos as instrucoes para redefinicao de senha."));

        then(forgotPasswordRequestMapper).should().toCommand(any(ForgotPasswordRequest.class));
        then(forgotPasswordUseCase).should().execute(command);
    }

    @Test
    void shouldReturn200WhenResetPasswordSucceeds() throws Exception {
        ResetPasswordRequest request = new ResetPasswordRequest("reset-token", "NovaSenha123");
        ResetPasswordCommand command = new ResetPasswordCommand("reset-token", "NovaSenha123");

        given(resetPasswordRequestMapper.toCommand(any(ResetPasswordRequest.class))).willReturn(command);

        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Senha redefinida com sucesso."));

        then(resetPasswordRequestMapper).should().toCommand(any(ResetPasswordRequest.class));
        then(resetPasswordUseCase).should().execute(command);
    }

    @Test
    void shouldReturn400WhenResetPasswordTokenIsInvalid() throws Exception {
        ResetPasswordRequest request = new ResetPasswordRequest("reset-token", "NovaSenha123");
        ResetPasswordCommand command = new ResetPasswordCommand("reset-token", "NovaSenha123");

        given(resetPasswordRequestMapper.toCommand(any(ResetPasswordRequest.class))).willReturn(command);
        willThrow(new InvalidPasswordResetTokenException())
                .given(resetPasswordUseCase)
                .execute(any(ResetPasswordCommand.class));

        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Token de redefinicao")));
    }

    @Test
    void shouldReturn401WhenRefreshTokenIsRevoked() throws Exception {
        given(refreshUseCase.execute(any(RefreshCommand.class))).willThrow(new TokenRevokedException());

        mockMvc.perform(post("/api/v1/auth/refresh"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message", containsString("Refresh token invalido")));
    }

    @Test
    void shouldReturn200WhenMeSucceeds() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = User.restore(
                userId,
                "Julio",
                "julio@email.com",
                "+5511999999999",
                "hash",
                Role.CLIENT,
                true,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
        UserResponse response = toUserResponse(user);

        mockAuthenticatedToken(CLIENT_TOKEN, userId, "julio@email.com", Role.CLIENT);
        given(getCurrentUserUseCase.execute(any(GetCurrentUserCommand.class))).willReturn(user);
        given(userMapper.toUserResponse(user)).willReturn(response);

        mockMvc.perform(get("/api/v1/auth/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(CLIENT_TOKEN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Perfil carregado com sucesso."))
                .andExpect(jsonPath("$.data.id").value(userId.toString()))
                .andExpect(jsonPath("$.data.email").value("julio@email.com"))
                .andExpect(jsonPath("$.data.role").value("CLIENT"));

        then(getCurrentUserUseCase).should().execute(any(GetCurrentUserCommand.class));
    }

    @Test
    void shouldReturn401WhenMeIsUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void shouldReturn403WhenAdminMeIsRequestedByNonAdmin() throws Exception {
        mockAuthenticatedToken(CLIENT_TOKEN, UUID.randomUUID(), "cliente@email.com", Role.CLIENT);

        mockMvc.perform(get("/api/v1/auth/admin/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(CLIENT_TOKEN)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void shouldReturn200WhenAdminMeIsRequestedByAdmin() throws Exception {
        UUID adminId = UUID.randomUUID();
        User admin = User.restore(
                adminId,
                "Admin",
                "admin@email.com",
                "+5511911111111",
                "hash",
                Role.ADMIN,
                true,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
        UserResponse response = toUserResponse(admin);

        mockAuthenticatedToken(ADMIN_TOKEN, adminId, "admin@email.com", Role.ADMIN);
        given(getCurrentUserUseCase.execute(any(GetCurrentUserCommand.class))).willReturn(admin);
        given(userMapper.toUserResponse(admin)).willReturn(response);

        mockMvc.perform(get("/api/v1/auth/admin/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(ADMIN_TOKEN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Perfil admin carregado com sucesso."))
                .andExpect(jsonPath("$.data.id").value(adminId.toString()))
                .andExpect(jsonPath("$.data.role").value("ADMIN"));
    }

    @Test
    void shouldReturn204AndClearCookieWhenLogoutHasRefreshToken() throws Exception {
        ResponseCookie clearCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/v1/auth/refresh")
                .maxAge(0)
                .build();

        mockAuthenticatedToken(CLIENT_TOKEN, UUID.randomUUID(), "cliente@email.com", Role.CLIENT);
        given(cookieUtils.buildClearRefreshTokenCookie()).willReturn(clearCookie);

        mockMvc.perform(post("/api/v1/auth/logout")
                        .header(HttpHeaders.AUTHORIZATION, bearer(CLIENT_TOKEN))
                        .cookie(new Cookie("refresh_token", "refresh-token")))
                .andExpect(status().isNoContent())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                        .string(HttpHeaders.SET_COOKIE, containsString("refresh_token=")))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                        .string(HttpHeaders.SET_COOKIE, containsString("Max-Age=0")));

        then(logoutUseCase).should().execute(any(LogoutCommand.class));
        then(cookieUtils).should().buildClearRefreshTokenCookie();
    }

    @Test
    void shouldReturn204AndClearCookieWhenLogoutHasNoCookieAndNoAuthentication() throws Exception {
        ResponseCookie clearCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/v1/auth/refresh")
                .maxAge(0)
                .build();

        given(cookieUtils.buildClearRefreshTokenCookie()).willReturn(clearCookie);

        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isNoContent())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                        .string(HttpHeaders.SET_COOKIE, containsString("refresh_token=")))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                        .string(HttpHeaders.SET_COOKIE, containsString("Max-Age=0")));

        then(logoutUseCase).should().execute(any(LogoutCommand.class));
        then(cookieUtils).should().buildClearRefreshTokenCookie();
    }

    @Test
    void shouldReturn204WhenLogoutHasRefreshTokenAndNoAuthentication() throws Exception {
        ResponseCookie clearCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/v1/auth/refresh")
                .maxAge(0)
                .build();

        given(cookieUtils.buildClearRefreshTokenCookie()).willReturn(clearCookie);

        mockMvc.perform(post("/api/v1/auth/logout")
                        .cookie(new Cookie("refresh_token", "refresh-token")))
                .andExpect(status().isNoContent())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                        .string(HttpHeaders.SET_COOKIE, containsString("refresh_token=")))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                        .string(HttpHeaders.SET_COOKIE, containsString("Max-Age=0")));

        then(logoutUseCase).should().execute(any(LogoutCommand.class));
        then(cookieUtils).should().buildClearRefreshTokenCookie();
    }

    private void mockAuthenticatedToken(String rawToken, UUID userId, String email, Role role) {
        AccessTokenClaims claims = new AccessTokenClaims(
                userId,
                email,
                role,
                UUID.randomUUID().toString(),
                Instant.now(),
                Instant.now().plusSeconds(900)
        );
        given(tokenGateway.validateToken(rawToken)).willReturn(true);
        given(tokenGateway.extractClaims(rawToken)).willReturn(claims);
    }

    private String bearer(String rawToken) {
        return "Bearer " + rawToken;
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getCreatedAt(),
                user.isActive()
        );
    }
}
