package com.servicepro;

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

    @Test
    void contextLoads() {
    }
}
