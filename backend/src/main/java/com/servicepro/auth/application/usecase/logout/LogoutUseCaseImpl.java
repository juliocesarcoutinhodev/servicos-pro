package com.servicepro.auth.application.usecase.logout;

import com.servicepro.auth.application.service.refreshtoken.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogoutUseCaseImpl implements LogoutUseCase {

    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public void execute(LogoutCommand command) {
        String rawRefreshToken = command.rawRefreshToken();
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            return;
        }

        refreshTokenService.revoke(rawRefreshToken);
    }
}
