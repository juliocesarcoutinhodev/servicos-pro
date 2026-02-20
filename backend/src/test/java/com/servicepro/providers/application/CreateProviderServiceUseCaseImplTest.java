package com.servicepro.providers.application;

import com.servicepro.catalog.domain.exception.ServiceCategoryNotFoundException;
import com.servicepro.catalog.domain.gateway.ServiceCategoryGateway;
import com.servicepro.providers.application.usecase.createproviderservice.CreateProviderServiceCommand;
import com.servicepro.providers.application.usecase.createproviderservice.CreateProviderServiceUseCase;
import com.servicepro.providers.application.usecase.createproviderservice.CreateProviderServiceUseCaseImpl;
import com.servicepro.providers.domain.gateway.ProviderServiceGateway;
import com.servicepro.providers.domain.model.ProviderService;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateProviderServiceUseCaseImplTest {

    @Mock
    private ProviderServiceGateway providerServiceGateway;

    @Mock
    private ServiceCategoryGateway serviceCategoryGateway;

    private CreateProviderServiceUseCase createProviderServiceUseCase;

    @BeforeEach
    void setUp() {
        createProviderServiceUseCase = new CreateProviderServiceUseCaseImpl(
                providerServiceGateway,
                serviceCategoryGateway
        );
    }

    @Test
    void shouldCreateProviderServiceWhenCategoryIsActive() {
        UUID providerId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        CreateProviderServiceCommand command = new CreateProviderServiceCommand(
                providerId,
                categoryId,
                "Instalacao de tomada",
                "Servico residencial",
                15000
        );

        when(serviceCategoryGateway.existsActiveById(categoryId)).thenReturn(true);
        when(providerServiceGateway.save(any(ProviderService.class))).thenAnswer(invocation -> {
            ProviderService service = invocation.getArgument(0);
            return ProviderService.restore(
                    UUID.randomUUID(),
                    service.getProviderId(),
                    service.getCategoryId(),
                    service.getName(),
                    service.getDescription(),
                    service.getPriceCents(),
                    service.isActive(),
                    OffsetDateTime.now(),
                    OffsetDateTime.now()
            );
        });

        ProviderService created = createProviderServiceUseCase.execute(command);

        ArgumentCaptor<ProviderService> captor = ArgumentCaptor.forClass(ProviderService.class);
        then(providerServiceGateway).should().save(captor.capture());
        ProviderService captured = captor.getValue();

        assertThat(captured.getProviderId()).isEqualTo(providerId);
        assertThat(captured.getCategoryId()).isEqualTo(categoryId);
        assertThat(captured.getName()).isEqualTo("Instalacao de tomada");
        assertThat(captured.getDescription()).isEqualTo("Servico residencial");
        assertThat(captured.getPriceCents()).isEqualTo(15000);
        assertThat(captured.isActive()).isTrue();

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Instalacao de tomada");
    }

    @Test
    void shouldThrowWhenCategoryIsNotActive() {
        CreateProviderServiceCommand command = new CreateProviderServiceCommand(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Instalacao de tomada",
                "Servico residencial",
                15000
        );

        when(serviceCategoryGateway.existsActiveById(command.categoryId())).thenReturn(false);

        assertThatThrownBy(() -> createProviderServiceUseCase.execute(command))
                .isInstanceOf(ServiceCategoryNotFoundException.class);

        then(providerServiceGateway).shouldHaveNoInteractions();
    }
}
