package com.servicepro;

import com.servicepro.auth.domain.gateway.RefreshTokenCacheGateway;
import com.servicepro.auth.domain.gateway.RefreshTokenGateway;
import com.servicepro.auth.domain.gateway.RateLimitGateway;
import com.servicepro.auth.domain.gateway.UserGateway;
import com.servicepro.auth.domain.gateway.PasswordResetTokenGateway;
import com.servicepro.catalog.domain.gateway.ServiceCategoryGateway;
import com.servicepro.providers.domain.gateway.ProviderDirectoryGateway;
import com.servicepro.providers.domain.gateway.ProviderServiceGateway;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ServiceProBackendApplicationTests {

    @MockitoBean
    private UserGateway userGateway;

    @MockitoBean
    private RefreshTokenGateway refreshTokenGateway;

    @MockitoBean
    private RefreshTokenCacheGateway refreshTokenCacheGateway;

    @MockitoBean
    private RateLimitGateway rateLimitGateway;

    @MockitoBean
    private PasswordResetTokenGateway passwordResetTokenGateway;

    @MockitoBean
    private ServiceCategoryGateway serviceCategoryGateway;

    @MockitoBean
    private ProviderServiceGateway providerServiceGateway;

    @MockitoBean
    private ProviderDirectoryGateway providerDirectoryGateway;

    @Test
    void contextLoads() {
    }
}
