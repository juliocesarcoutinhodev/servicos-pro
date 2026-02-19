package com.servicepro.auth.application;

import com.servicepro.auth.application.service.refreshtoken.RefreshTokenService;
import com.servicepro.auth.application.usecase.logout.LogoutCommand;
import com.servicepro.auth.application.usecase.logout.LogoutUseCase;
import com.servicepro.auth.application.usecase.logout.LogoutUseCaseImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class LogoutUseCaseImplTest {

    @Mock
    private RefreshTokenService refreshTokenService;

    private LogoutUseCase logoutUseCase;

    @BeforeEach
    void setUp() {
        logoutUseCase = new LogoutUseCaseImpl(refreshTokenService);
    }

    @Test
    void shouldRevokeRefreshTokenWhenTokenIsProvided() {
        String rawRefreshToken = "refresh-token";

        logoutUseCase.execute(new LogoutCommand(rawRefreshToken));

        then(refreshTokenService).should().revoke(rawRefreshToken);
    }

    @Test
    void shouldIgnoreLogoutWhenRefreshTokenIsMissing() {
        logoutUseCase.execute(new LogoutCommand(null));
        logoutUseCase.execute(new LogoutCommand(""));
        logoutUseCase.execute(new LogoutCommand("   "));

        then(refreshTokenService).shouldHaveNoInteractions();
    }
}
