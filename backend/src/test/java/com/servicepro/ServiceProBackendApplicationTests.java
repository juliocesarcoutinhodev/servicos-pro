package com.servicepro;

import com.servicepro.auth.domain.gateway.RefreshTokenCacheGateway;
import com.servicepro.auth.domain.gateway.RefreshTokenGateway;
import com.servicepro.auth.domain.gateway.RateLimitGateway;
import com.servicepro.auth.domain.gateway.UserGateway;
import com.servicepro.auth.domain.gateway.PasswordResetTokenGateway;
import com.servicepro.catalog.domain.gateway.ServiceCategoryGateway;
import com.servicepro.providers.domain.gateway.ProviderServiceGateway;
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

    @MockBean
    private RateLimitGateway rateLimitGateway;

    @MockBean
    private PasswordResetTokenGateway passwordResetTokenGateway;

    @MockBean
    private ServiceCategoryGateway serviceCategoryGateway;

    @MockBean
    private ProviderServiceGateway providerServiceGateway;

    @Test
    void contextLoads() {
    }
}
