package com.servicepro.auth.interfaces;

import com.servicepro.auth.application.usecase.login.LoginCommand;
import com.servicepro.auth.application.usecase.login.LoginResult;
import com.servicepro.auth.application.usecase.login.LoginUseCase;
import com.servicepro.auth.application.usecase.login.TokenPair;
import com.servicepro.auth.application.usecase.forgotpassword.ForgotPasswordCommand;
import com.servicepro.auth.application.usecase.forgotpassword.ForgotPasswordUseCase;
import com.servicepro.auth.application.usecase.me.GetCurrentUserCommand;
import com.servicepro.auth.application.usecase.me.GetCurrentUserUseCase;
import com.servicepro.auth.application.usecase.logout.LogoutCommand;
import com.servicepro.auth.application.usecase.logout.LogoutUseCase;
import com.servicepro.auth.application.usecase.refresh.RefreshCommand;
import com.servicepro.auth.application.usecase.refresh.RefreshResult;
import com.servicepro.auth.application.usecase.refresh.RefreshUseCase;
import com.servicepro.auth.application.usecase.resetpassword.ResetPasswordCommand;
import com.servicepro.auth.application.usecase.resetpassword.ResetPasswordUseCase;
import com.servicepro.auth.application.usecase.signup.SignupCommand;
import com.servicepro.auth.application.usecase.signup.SignupUseCase;
import com.servicepro.auth.domain.exception.InvalidCredentialsException;
import com.servicepro.auth.infrastructure.security.CookieUtils;
import com.servicepro.auth.infrastructure.security.AuthenticatedUserPrincipal;
import com.servicepro.auth.interfaces.dto.ForgotPasswordRequest;
import com.servicepro.auth.interfaces.dto.LoginRequest;
import com.servicepro.auth.domain.model.User;
import com.servicepro.auth.interfaces.dto.ResetPasswordRequest;
import com.servicepro.auth.interfaces.dto.SignupRequest;
import com.servicepro.auth.interfaces.dto.UserResponse;
import com.servicepro.auth.interfaces.mapper.ForgotPasswordRequestMapper;
import com.servicepro.auth.interfaces.mapper.LoginRequestMapper;
import com.servicepro.auth.interfaces.mapper.ResetPasswordRequestMapper;
import com.servicepro.auth.interfaces.mapper.SignupRequestMapper;
import com.servicepro.auth.interfaces.mapper.UserMapper;
import com.servicepro.shared.interfaces.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SignupUseCase signupUseCase;
    private final LoginUseCase loginUseCase;
    private final ForgotPasswordUseCase forgotPasswordUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;
    private final RefreshUseCase refreshUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final LogoutUseCase logoutUseCase;
    private final SignupRequestMapper signupRequestMapper;
    private final LoginRequestMapper loginRequestMapper;
    private final ForgotPasswordRequestMapper forgotPasswordRequestMapper;
    private final ResetPasswordRequestMapper resetPasswordRequestMapper;
    private final UserMapper userMapper;
    private final CookieUtils cookieUtils;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponse>> signup(@Valid @RequestBody SignupRequest request) {
        log.info("Signup request received.");

        SignupCommand command = signupRequestMapper.toCommand(request);
        User user = signupUseCase.execute(command);
        UserResponse response = userMapper.toUserResponse(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(HttpStatus.CREATED, "Usuario cadastrado com sucesso.", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenPair>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received.");

        LoginCommand command = loginRequestMapper.toCommand(request);
        LoginResult result = loginUseCase.execute(command);
        ResponseCookie refreshCookie = cookieUtils.buildRefreshTokenCookie(result.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(ApiResponse.of(HttpStatus.OK, "Login realizado com sucesso.", result.tokenPair()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenPair>> refresh(
            @CookieValue(name = CookieUtils.REFRESH_COOKIE_NAME, required = false) String refreshToken
    ) {
        log.info("Refresh token request received.");

        RefreshResult result = refreshUseCase.execute(new RefreshCommand(refreshToken));
        ResponseCookie refreshCookie = cookieUtils.buildRefreshTokenCookie(result.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(ApiResponse.of(HttpStatus.OK, "Token atualizado com sucesso.", result.tokenPair()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        ForgotPasswordCommand command = forgotPasswordRequestMapper.toCommand(request);
        forgotPasswordUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponse.of(
                        HttpStatus.ACCEPTED,
                        "Se o email estiver cadastrado, enviaremos as instrucoes para redefinicao de senha.",
                        null
                ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        ResetPasswordCommand command = resetPasswordRequestMapper.toCommand(request);
        resetPasswordUseCase.execute(command);

        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK, "Senha redefinida com sucesso.", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK, "Perfil carregado com sucesso.", currentUserResponse(authentication)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/me")
    public ResponseEntity<ApiResponse<UserResponse>> adminMe(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK, "Perfil admin carregado com sucesso.", currentUserResponse(authentication)));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = CookieUtils.REFRESH_COOKIE_NAME, required = false) String refreshToken
    ) {
        log.info("Logout request received.");

        logoutUseCase.execute(new LogoutCommand(refreshToken));
        ResponseCookie clearRefreshCookie = cookieUtils.buildClearRefreshTokenCookie();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, clearRefreshCookie.toString())
                .build();
    }

    private UserResponse currentUserResponse(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUserPrincipal principal)) {
            throw new InvalidCredentialsException();
        }

        User user = getCurrentUserUseCase.execute(new GetCurrentUserCommand(principal.userId()));
        return userMapper.toUserResponse(user);
    }
}
