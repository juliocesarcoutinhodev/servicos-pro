package com.servicepro.providers.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.servicepro.auth.application.service.ratelimit.AuthRateLimitService;
import com.servicepro.auth.domain.gateway.TokenGateway;
import com.servicepro.auth.domain.model.AccessTokenClaims;
import com.servicepro.auth.domain.model.Role;
import com.servicepro.auth.infrastructure.config.SecurityConfig;
import com.servicepro.catalog.application.usecase.createcategory.CreateServiceCategoryCommand;
import com.servicepro.catalog.application.usecase.createcategory.CreateServiceCategoryUseCase;
import com.servicepro.catalog.application.usecase.listcategories.ListServiceCategoriesUseCase;
import com.servicepro.catalog.domain.model.ServiceCategory;
import com.servicepro.catalog.interfaces.ServiceCategoryController;
import com.servicepro.catalog.interfaces.dto.CreateServiceCategoryRequest;
import com.servicepro.catalog.interfaces.dto.ServiceCategoryResponse;
import com.servicepro.catalog.interfaces.mapper.CreateServiceCategoryRequestMapper;
import com.servicepro.catalog.interfaces.mapper.ServiceCategoryResponseMapper;
import com.servicepro.providers.application.usecase.createproviderservice.CreateProviderServiceCommand;
import com.servicepro.providers.application.usecase.createproviderservice.CreateProviderServiceUseCase;
import com.servicepro.providers.application.usecase.deleteproviderservice.DeleteProviderServiceUseCase;
import com.servicepro.providers.application.usecase.listproviderservices.ListProviderServicesUseCase;
import com.servicepro.providers.application.usecase.updateproviderservice.UpdateProviderServiceUseCase;
import com.servicepro.providers.domain.model.ProviderService;
import com.servicepro.providers.interfaces.dto.CreateProviderServiceRequest;
import com.servicepro.providers.interfaces.dto.ProviderServiceResponse;
import com.servicepro.providers.interfaces.mapper.CreateProviderServiceRequestMapper;
import com.servicepro.providers.interfaces.mapper.ProviderServiceResponseMapper;
import com.servicepro.providers.interfaces.mapper.UpdateProviderServiceRequestMapper;
import com.servicepro.shared.infrastructure.security.RestAccessDeniedHandler;
import com.servicepro.shared.infrastructure.security.RestAuthenticationEntryPoint;
import com.servicepro.shared.interfaces.exception.GenericExceptionHandler;
import com.servicepro.shared.interfaces.exception.NegocioExceptionHandler;
import com.servicepro.shared.interfaces.exception.ValidacaoExceptionHandler;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ProviderServiceController.class, ServiceCategoryController.class})
@Import({
        SecurityConfig.class,
        RestAuthenticationEntryPoint.class,
        RestAccessDeniedHandler.class,
        NegocioExceptionHandler.class,
        ValidacaoExceptionHandler.class,
        GenericExceptionHandler.class
})
@ActiveProfiles("test")
class ProviderServiceControllerTest {

    private static final String PROVIDER_TOKEN = "provider-token";
    private static final String CLIENT_TOKEN = "client-token";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateProviderServiceUseCase createProviderServiceUseCase;

    @MockBean
    private ListProviderServicesUseCase listProviderServicesUseCase;

    @MockBean
    private UpdateProviderServiceUseCase updateProviderServiceUseCase;

    @MockBean
    private DeleteProviderServiceUseCase deleteProviderServiceUseCase;

    @MockBean
    private CreateProviderServiceRequestMapper createProviderServiceRequestMapper;

    @MockBean
    private UpdateProviderServiceRequestMapper updateProviderServiceRequestMapper;

    @MockBean
    private ProviderServiceResponseMapper providerServiceResponseMapper;

    @MockBean
    private ListServiceCategoriesUseCase listServiceCategoriesUseCase;

    @MockBean
    private CreateServiceCategoryUseCase createServiceCategoryUseCase;

    @MockBean
    private CreateServiceCategoryRequestMapper createServiceCategoryRequestMapper;

    @MockBean
    private ServiceCategoryResponseMapper serviceCategoryResponseMapper;

    @MockBean
    private AuthRateLimitService authRateLimitService;

    @MockBean
    private TokenGateway tokenGateway;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void shouldAllowPublicCategoriesWithoutAuthentication() throws Exception {
        ServiceCategory category = ServiceCategory.restore(
                UUID.fromString("10000000-0000-0000-0000-000000000001"),
                "Eletricista",
                "eletricista",
                "Instalacao e manutencao eletrica.",
                true,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        ServiceCategoryResponse response = new ServiceCategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription()
        );

        when(listServiceCategoriesUseCase.execute()).thenReturn(List.of(category));
        when(serviceCategoryResponseMapper.toResponseList(anyList())).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/services/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data[0].name").value("Eletricista"));
    }

    @Test
    void shouldCreateCategoryWhenAuthenticatedAsProvider() throws Exception {
        UUID providerId = UUID.randomUUID();
        mockAuthenticatedToken(PROVIDER_TOKEN, providerId, "provider@email.com", Role.PROVIDER);

        CreateServiceCategoryRequest request = new CreateServiceCategoryRequest(
                "Mecanico",
                "Reparos automotivos em geral"
        );

        CreateServiceCategoryCommand command = new CreateServiceCategoryCommand(
                "Mecanico",
                "Reparos automotivos em geral"
        );

        ServiceCategory createdCategory = ServiceCategory.restore(
                UUID.randomUUID(),
                "Mecanico",
                "mecanico",
                "Reparos automotivos em geral",
                true,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        ServiceCategoryResponse response = new ServiceCategoryResponse(
                createdCategory.getId(),
                createdCategory.getName(),
                createdCategory.getDescription()
        );

        when(createServiceCategoryRequestMapper.toCommand(any(CreateServiceCategoryRequest.class)))
                .thenReturn(command);
        when(createServiceCategoryUseCase.execute(command)).thenReturn(createdCategory);
        when(serviceCategoryResponseMapper.toResponse(createdCategory)).thenReturn(response);

        mockMvc.perform(post("/api/v1/services/categories")
                        .header(HttpHeaders.AUTHORIZATION, bearer(PROVIDER_TOKEN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.name").value("Mecanico"));
    }

    @Test
    void shouldReturn401WhenListingMyServicesWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/providers/services"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403WhenClientTriesToListProviderServices() throws Exception {
        mockAuthenticatedToken(CLIENT_TOKEN, Role.CLIENT);

        mockMvc.perform(get("/api/v1/providers/services")
                        .header(HttpHeaders.AUTHORIZATION, bearer(CLIENT_TOKEN)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldCreateProviderServiceWhenAuthenticatedAsProvider() throws Exception {
        UUID providerId = UUID.randomUUID();
        UUID categoryId = UUID.fromString("10000000-0000-0000-0000-000000000001");
        UUID serviceId = UUID.randomUUID();

        mockAuthenticatedToken(PROVIDER_TOKEN, providerId, "provider@email.com", Role.PROVIDER);

        CreateProviderServiceRequest request = new CreateProviderServiceRequest(
                categoryId,
                "Instalacao de tomada",
                "Servico residencial",
                15000L
        );

        CreateProviderServiceCommand command = new CreateProviderServiceCommand(
                providerId,
                categoryId,
                "Instalacao de tomada",
                "Servico residencial",
                15000
        );

        ProviderService providerService = ProviderService.restore(
                serviceId,
                providerId,
                categoryId,
                "Instalacao de tomada",
                "Servico residencial",
                15000,
                true,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        ProviderServiceResponse response = new ProviderServiceResponse(
                serviceId,
                categoryId,
                "Instalacao de tomada",
                "Servico residencial",
                15000,
                true,
                providerService.getCreatedAt(),
                providerService.getUpdatedAt()
        );

        when(createProviderServiceRequestMapper.toCommand(any(CreateProviderServiceRequest.class), any(UUID.class)))
                .thenReturn(command);
        when(createProviderServiceUseCase.execute(command)).thenReturn(providerService);
        when(providerServiceResponseMapper.toResponse(providerService)).thenReturn(response);

        mockMvc.perform(post("/api/v1/providers/services")
                        .header(HttpHeaders.AUTHORIZATION, bearer(PROVIDER_TOKEN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.id").value(serviceId.toString()))
                .andExpect(jsonPath("$.data.name").value("Instalacao de tomada"))
                .andExpect(jsonPath("$.data.priceCents").value(15000));
    }

    private void mockAuthenticatedToken(String rawToken, Role role) {
        mockAuthenticatedToken(rawToken, UUID.randomUUID(), "user@email.com", role);
    }

    private void mockAuthenticatedToken(String rawToken, UUID userId, String email, Role role) {
        AccessTokenClaims claims = new AccessTokenClaims(
                userId,
                email,
                role,
                UUID.randomUUID().toString(),
                Instant.now(),
                Instant.now().plusSeconds(900)
        );
        when(tokenGateway.validateToken(rawToken)).thenReturn(true);
        when(tokenGateway.extractClaims(rawToken)).thenReturn(claims);
    }

    private String bearer(String rawToken) {
        return "Bearer " + rawToken;
    }
}
