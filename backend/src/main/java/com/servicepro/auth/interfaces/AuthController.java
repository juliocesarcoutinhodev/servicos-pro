package com.servicepro.auth.interfaces;

import com.servicepro.auth.application.usecase.login.LoginCommand;
import com.servicepro.auth.application.usecase.login.LoginResult;
import com.servicepro.auth.application.usecase.login.LoginUseCase;
import com.servicepro.auth.application.usecase.login.TokenPair;
import com.servicepro.auth.application.usecase.signup.SignupCommand;
import com.servicepro.auth.application.usecase.signup.SignupUseCase;
import com.servicepro.auth.infrastructure.security.CookieUtils;
import com.servicepro.auth.interfaces.dto.LoginRequest;
import com.servicepro.auth.domain.model.User;
import com.servicepro.auth.interfaces.dto.SignupRequest;
import com.servicepro.auth.interfaces.dto.UserResponse;
import com.servicepro.auth.interfaces.mapper.LoginRequestMapper;
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
    private final SignupRequestMapper signupRequestMapper;
    private final LoginRequestMapper loginRequestMapper;
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
}
