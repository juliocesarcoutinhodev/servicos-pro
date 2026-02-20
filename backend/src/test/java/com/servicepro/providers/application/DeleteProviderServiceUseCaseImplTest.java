package com.servicepro.providers.application;

import com.servicepro.providers.application.usecase.deleteproviderservice.DeleteProviderServiceCommand;
import com.servicepro.providers.application.usecase.deleteproviderservice.DeleteProviderServiceUseCase;
import com.servicepro.providers.application.usecase.deleteproviderservice.DeleteProviderServiceUseCaseImpl;
import com.servicepro.providers.domain.exception.ProviderServiceNotFoundException;
import com.servicepro.providers.domain.gateway.ProviderServiceGateway;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteProviderServiceUseCaseImplTest {

    @Mock
    private ProviderServiceGateway providerServiceGateway;

    private DeleteProviderServiceUseCase deleteProviderServiceUseCase;

    @BeforeEach
    void setUp() {
        deleteProviderServiceUseCase = new DeleteProviderServiceUseCaseImpl(providerServiceGateway);
    }

    @Test
    void shouldDeleteProviderServiceWhenExists() {
        DeleteProviderServiceCommand command = new DeleteProviderServiceCommand(UUID.randomUUID(), UUID.randomUUID());

        when(providerServiceGateway.deleteByIdAndProviderId(command.serviceId(), command.providerId())).thenReturn(true);

        deleteProviderServiceUseCase.execute(command);

        then(providerServiceGateway).should().deleteByIdAndProviderId(command.serviceId(), command.providerId());
    }

    @Test
    void shouldThrowWhenProviderServiceDoesNotExist() {
        DeleteProviderServiceCommand command = new DeleteProviderServiceCommand(UUID.randomUUID(), UUID.randomUUID());

        when(providerServiceGateway.deleteByIdAndProviderId(command.serviceId(), command.providerId())).thenReturn(false);

        assertThatThrownBy(() -> deleteProviderServiceUseCase.execute(command))
                .isInstanceOf(ProviderServiceNotFoundException.class);
    }
}
