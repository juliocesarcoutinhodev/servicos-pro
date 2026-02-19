package com.servicepro.auth.interfaces;

import com.servicepro.auth.application.usecase.signup.SignupCommand;
import com.servicepro.auth.application.usecase.signup.SignupUseCase;
import com.servicepro.auth.domain.model.User;
import com.servicepro.auth.interfaces.dto.SignupRequest;
import com.servicepro.auth.interfaces.dto.UserResponse;
import com.servicepro.auth.interfaces.mapper.SignupRequestMapper;
import com.servicepro.auth.interfaces.mapper.UserMapper;
import com.servicepro.shared.interfaces.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    private final SignupRequestMapper signupRequestMapper;
    private final UserMapper userMapper;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponse>> signup(@Valid @RequestBody SignupRequest request) {
        log.info("Signup request received for email {}", request.email());

        SignupCommand command = signupRequestMapper.toCommand(request);
        User user = signupUseCase.execute(command);
        UserResponse response = userMapper.toUserResponse(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(HttpStatus.CREATED, "Usuario cadastrado com sucesso.", response));
    }
}
