package com.servicepro.providers.application;

import com.servicepro.catalog.domain.exception.ServiceCategoryNotFoundException;
import com.servicepro.catalog.domain.gateway.ServiceCategoryGateway;
import com.servicepro.providers.application.usecase.updateproviderservice.UpdateProviderServiceCommand;
import com.servicepro.providers.application.usecase.updateproviderservice.UpdateProviderServiceUseCase;
import com.servicepro.providers.application.usecase.updateproviderservice.UpdateProviderServiceUseCaseImpl;
import com.servicepro.providers.domain.exception.ProviderServiceNotFoundException;
import com.servicepro.providers.domain.gateway.ProviderServiceGateway;
import com.servicepro.providers.domain.model.ProviderService;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateProviderServiceUseCaseImplTest {

    @Mock
    private ProviderServiceGateway providerServiceGateway;

    @Mock
    private ServiceCategoryGateway serviceCategoryGateway;

    private UpdateProviderServiceUseCase updateProviderServiceUseCase;

    @BeforeEach
    void setUp() {
        updateProviderServiceUseCase = new UpdateProviderServiceUseCaseImpl(
                providerServiceGateway,
                serviceCategoryGateway
        );
    }

    @Test
    void shouldUpdateProviderServiceWhenServiceBelongsToProviderAndCategoryIsActive() {
        UUID providerId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        UUID oldCategoryId = UUID.randomUUID();
        UUID newCategoryId = UUID.randomUUID();

        ProviderService existing = ProviderService.restore(
                serviceId,
                providerId,
                oldCategoryId,
                "Servico antigo",
                "Descricao antiga",
                10000,
                true,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        UpdateProviderServiceCommand command = new UpdateProviderServiceCommand(
                providerId,
                serviceId,
                newCategoryId,
                "Servico atualizado",
                "Descricao atualizada",
                20000
        );

        when(providerServiceGateway.findByIdAndProviderId(serviceId, providerId)).thenReturn(Optional.of(existing));
        when(serviceCategoryGateway.existsActiveById(newCategoryId)).thenReturn(true);
        when(providerServiceGateway.save(any(ProviderService.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProviderService updated = updateProviderServiceUseCase.execute(command);

        assertThat(updated.getId()).isEqualTo(serviceId);
        assertThat(updated.getProviderId()).isEqualTo(providerId);
        assertThat(updated.getCategoryId()).isEqualTo(newCategoryId);
        assertThat(updated.getName()).isEqualTo("Servico atualizado");
        assertThat(updated.getDescription()).isEqualTo("Descricao atualizada");
        assertThat(updated.getPriceCents()).isEqualTo(20000);
    }

    @Test
    void shouldThrowWhenProviderServiceDoesNotExist() {
        UpdateProviderServiceCommand command = new UpdateProviderServiceCommand(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Servico atualizado",
                "Descricao atualizada",
                20000
        );

        when(providerServiceGateway.findByIdAndProviderId(command.serviceId(), command.providerId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateProviderServiceUseCase.execute(command))
                .isInstanceOf(ProviderServiceNotFoundException.class);

        then(serviceCategoryGateway).shouldHaveNoInteractions();
    }

    @Test
    void shouldThrowWhenCategoryIsNotActive() {
        UUID providerId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        ProviderService existing = ProviderService.restore(
                serviceId,
                providerId,
                UUID.randomUUID(),
                "Servico antigo",
                "Descricao antiga",
                10000,
                true,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        UpdateProviderServiceCommand command = new UpdateProviderServiceCommand(
                providerId,
                serviceId,
                categoryId,
                "Servico atualizado",
                "Descricao atualizada",
                20000
        );

        when(providerServiceGateway.findByIdAndProviderId(serviceId, providerId)).thenReturn(Optional.of(existing));
        when(serviceCategoryGateway.existsActiveById(categoryId)).thenReturn(false);

        assertThatThrownBy(() -> updateProviderServiceUseCase.execute(command))
                .isInstanceOf(ServiceCategoryNotFoundException.class);

        then(providerServiceGateway).should().findByIdAndProviderId(serviceId, providerId);
        then(providerServiceGateway).should(never()).save(any(ProviderService.class));
    }
}
