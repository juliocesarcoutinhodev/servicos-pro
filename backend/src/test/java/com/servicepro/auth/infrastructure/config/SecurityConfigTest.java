package com.servicepro.auth.infrastructure.config;

import com.servicepro.auth.application.service.ratelimit.AuthRateLimitAction;
import com.servicepro.auth.application.service.ratelimit.AuthRateLimitService;
import com.servicepro.auth.application.service.ratelimit.RateLimitStatus;
import com.servicepro.auth.application.usecase.login.LoginUseCase;
import com.servicepro.auth.application.usecase.logout.LogoutUseCase;
import com.servicepro.auth.application.usecase.me.GetCurrentUserUseCase;
import com.servicepro.auth.application.usecase.refresh.RefreshUseCase;
import com.servicepro.auth.application.usecase.signup.SignupUseCase;
import com.servicepro.auth.domain.gateway.TokenGateway;
import com.servicepro.auth.domain.model.AccessTokenClaims;
import com.servicepro.auth.domain.model.Role;
import com.servicepro.auth.domain.model.User;
import com.servicepro.auth.infrastructure.security.CookieUtils;
import com.servicepro.auth.interfaces.AuthController;
import com.servicepro.auth.interfaces.dto.UserResponse;
import com.servicepro.auth.interfaces.mapper.LoginRequestMapper;
import com.servicepro.auth.interfaces.mapper.SignupRequestMapper;
import com.servicepro.auth.interfaces.mapper.UserMapper;
import com.servicepro.shared.infrastructure.security.RestAccessDeniedHandler;
import com.servicepro.shared.infrastructure.security.RestAuthenticationEntryPoint;
import com.servicepro.shared.interfaces.exception.GenericExceptionHandler;
import com.servicepro.shared.interfaces.exception.NegocioExceptionHandler;
import com.servicepro.shared.interfaces.exception.ValidacaoExceptionHandler;
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
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
class SecurityConfigTest {

    private static final String CLIENT_TOKEN = "client-token";
    private static final String ADMIN_TOKEN = "admin-token";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SignupUseCase signupUseCase;

    @MockBean
    private LoginUseCase loginUseCase;

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
    void shouldPermitPublicLoginWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldAllowPreflightForConfiguredCorsOrigin() throws Exception {
        mockMvc.perform(options("/api/v1/auth/login")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5173")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5173"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));
    }

    @Test
    void shouldRejectPreflightForUnknownCorsOrigin() throws Exception {
        mockMvc.perform(options("/api/v1/auth/login")
                        .header(HttpHeaders.ORIGIN, "https://evil.example")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldPermitLogoutWithoutAuthentication() throws Exception {
        ResponseCookie clearCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/v1/auth/refresh")
                .maxAge(0)
                .build();
        given(cookieUtils.buildClearRefreshTokenCookie()).willReturn(clearCookie);

        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn401WhenMeEndpointHasNoToken() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void shouldReturn200WhenMeEndpointHasClientToken() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = user(userId, Role.CLIENT, true);

        mockAuthenticatedToken(CLIENT_TOKEN, userId, "cliente@email.com", Role.CLIENT);
        given(getCurrentUserUseCase.execute(any())).willReturn(user);
        given(userMapper.toUserResponse(user)).willReturn(toUserResponse(user));

        mockMvc.perform(get("/api/v1/auth/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(CLIENT_TOKEN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(userId.toString()))
                .andExpect(jsonPath("$.data.role").value("CLIENT"));
    }

    @Test
    void shouldReturn403WhenAdminEndpointReceivesClientToken() throws Exception {
        mockAuthenticatedToken(CLIENT_TOKEN, UUID.randomUUID(), "cliente@email.com", Role.CLIENT);

        mockMvc.perform(get("/api/v1/auth/admin/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(CLIENT_TOKEN)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void shouldReturn200WhenAdminEndpointReceivesAdminToken() throws Exception {
        UUID adminId = UUID.randomUUID();
        User admin = user(adminId, Role.ADMIN, true);

        mockAuthenticatedToken(ADMIN_TOKEN, adminId, "admin@email.com", Role.ADMIN);
        given(getCurrentUserUseCase.execute(any())).willReturn(admin);
        given(userMapper.toUserResponse(admin)).willReturn(toUserResponse(admin));

        mockMvc.perform(get("/api/v1/auth/admin/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(ADMIN_TOKEN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(adminId.toString()))
                .andExpect(jsonPath("$.data.role").value("ADMIN"));
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

    private User user(UUID userId, Role role, boolean active) {
        return User.restore(
                userId,
                role == Role.ADMIN ? "Admin" : "Cliente",
                role == Role.ADMIN ? "admin@email.com" : "cliente@email.com",
                "+5511999999999",
                "hash",
                role,
                active,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
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
