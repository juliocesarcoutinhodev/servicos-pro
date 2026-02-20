package com.servicepro.providers.application;

import com.servicepro.providers.application.usecase.listproviders.ListProvidersCommand;
import com.servicepro.providers.application.usecase.listproviders.ListProvidersUseCase;
import com.servicepro.providers.application.usecase.listproviders.ListProvidersUseCaseImpl;
import com.servicepro.providers.domain.gateway.ProviderDirectoryGateway;
import com.servicepro.providers.domain.model.ProviderSummary;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ListProvidersUseCaseImplTest {

    @Mock
    private ProviderDirectoryGateway providerDirectoryGateway;

    private ListProvidersUseCase listProvidersUseCase;

    @BeforeEach
    void setUp() {
        listProvidersUseCase = new ListProvidersUseCaseImpl(providerDirectoryGateway);
    }

    @Test
    void shouldNormalizePageAndSizeBeforeDelegating() {
        UUID providerId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        ProviderSummary providerSummary = new ProviderSummary(
                providerId,
                "Prestador 1",
                List.of("Eletricista"),
                null,
                0,
                true,
                2
        );

        Page<ProviderSummary> expectedPage = new PageImpl<>(
                List.of(providerSummary),
                PageRequest.of(0, 10),
                1
        );

        given(providerDirectoryGateway.findActiveProvidersWithServices(categoryId, PageRequest.of(0, 10)))
                .willReturn(expectedPage);

        Page<ProviderSummary> result = listProvidersUseCase.execute(new ListProvidersCommand(-5, 0, categoryId));

        assertThat(result).isEqualTo(expectedPage);
        then(providerDirectoryGateway).should().findActiveProvidersWithServices(categoryId, PageRequest.of(0, 10));
    }

    @Test
    void shouldCapSizeAtMaxAllowedValue() {
        UUID categoryId = UUID.randomUUID();
        Page<ProviderSummary> expectedPage = new PageImpl<>(
                List.of(),
                PageRequest.of(2, 100),
                0
        );

        given(providerDirectoryGateway.findActiveProvidersWithServices(categoryId, PageRequest.of(2, 100)))
                .willReturn(expectedPage);

        Page<ProviderSummary> result = listProvidersUseCase.execute(new ListProvidersCommand(2, 500, categoryId));

        assertThat(result).isEqualTo(expectedPage);
        then(providerDirectoryGateway).should().findActiveProvidersWithServices(categoryId, PageRequest.of(2, 100));
    }
}
