package com.servicepro;

import com.servicepro.auth.domain.gateway.RefreshTokenCacheGateway;
import com.servicepro.auth.domain.gateway.RefreshTokenGateway;
import com.servicepro.auth.domain.gateway.UserGateway;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ServiceProBackendApplicationTests {

    @MockBean
    private UserGateway userGateway;

    @MockBean
    private RefreshTokenGateway refreshTokenGateway;

    @MockBean
    private RefreshTokenCacheGateway refreshTokenCacheGateway;

    @Test
    void contextLoads() {
    }
}
