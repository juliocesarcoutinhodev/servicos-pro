package com.servicepro.foundation;

import com.servicepro.auth.domain.gateway.RefreshTokenCacheGateway;
import com.servicepro.auth.domain.gateway.RefreshTokenGateway;
import com.servicepro.auth.domain.gateway.UserGateway;
import com.servicepro.shared.domain.exception.NegocioException;
import com.servicepro.shared.domain.exception.RecursoNaoEncontradoException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(FoundationErrorHandlingIntegrationTest.TestControllers.class)
@ActiveProfiles("test")
class FoundationErrorHandlingIntegrationTest {

    @MockBean
    private UserGateway userGateway;

    @MockBean
    private RefreshTokenGateway refreshTokenGateway;

    @MockBean
    private RefreshTokenCacheGateway refreshTokenCacheGateway;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "CLIENT")
    void shouldReturn400ForBusinessException() throws Exception {
        mockMvc.perform(get("/api/v1/foundation/bad-request"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.path").value("/api/v1/foundation/bad-request"));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void shouldReturn404ForNotFoundException() throws Exception {
        mockMvc.perform(get("/api/v1/foundation/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.path").value("/api/v1/foundation/not-found"));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void shouldReturn500ForUnexpectedError() throws Exception {
        mockMvc.perform(get("/api/v1/foundation/internal"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.path").value("/api/v1/foundation/internal"));
    }

    @Test
    void shouldReturn401ForUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get("/api/v1/foundation/admin-only"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.path").value("/api/v1/foundation/admin-only"));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void shouldReturn403ForForbiddenRequest() throws Exception {
        mockMvc.perform(get("/api/v1/foundation/admin-only"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.path").value("/api/v1/foundation/admin-only"));
    }

    @TestConfiguration
    static class TestControllers {

        @Bean
        FoundationTestController foundationTestController() {
            return new FoundationTestController();
        }
    }

    @RestController
    @RequestMapping("/api/v1/foundation")
    static class FoundationTestController {

        @GetMapping("/bad-request")
        ResponseEntity<Void> badRequest() {
            throw new NegocioException("Regra de negocio invalida.");
        }

        @GetMapping("/not-found")
        ResponseEntity<Void> notFound() {
            throw new RecursoNaoEncontradoException("Recurso nao encontrado.");
        }

        @GetMapping("/internal")
        ResponseEntity<Void> internal() {
            throw new IllegalStateException("unexpected");
        }

        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping("/admin-only")
        ResponseEntity<String> adminOnly() {
            return ResponseEntity.status(HttpStatus.OK).body("ok");
        }
    }
}
