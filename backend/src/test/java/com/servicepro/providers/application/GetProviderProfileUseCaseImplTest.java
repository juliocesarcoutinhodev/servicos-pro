package com.servicepro.providers.application;

import com.servicepro.providers.application.usecase.getproviderprofile.GetProviderProfileCommand;
import com.servicepro.providers.application.usecase.getproviderprofile.GetProviderProfileUseCase;
import com.servicepro.providers.application.usecase.getproviderprofile.GetProviderProfileUseCaseImpl;
import com.servicepro.providers.domain.exception.ProviderNotFoundException;
import com.servicepro.providers.domain.gateway.ProviderProfileGateway;
import com.servicepro.providers.domain.model.ProviderPublicProfile;
import com.servicepro.providers.domain.model.ProviderPublicService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class GetProviderProfileUseCaseImplTest {

    @Mock
    private ProviderProfileGateway providerProfileGateway;

    private GetProviderProfileUseCase getProviderProfileUseCase;

    @BeforeEach
    void setUp() {
        getProviderProfileUseCase = new GetProviderProfileUseCaseImpl(providerProfileGateway);
    }

    @Test
    void shouldReturnProviderProfileWhenProviderExists() {
        UUID providerId = UUID.randomUUID();
        ProviderPublicProfile profile = new ProviderPublicProfile(
                providerId,
                "Joao Santos",
                List.of("Eletricista"),
                null,
                4.8,
                127,
                null,
                null,
                true,
                List.of(new ProviderPublicService(
                        UUID.randomUUID(),
                        "Instalacao de tomadas",
                        5000L,
                        "Servico residencial"
                ))
        );

        given(providerProfileGateway.findActiveProviderPublicProfileById(providerId)).willReturn(Optional.of(profile));

        ProviderPublicProfile result = getProviderProfileUseCase.execute(new GetProviderProfileCommand(providerId));

        assertThat(result).isEqualTo(profile);
        then(providerProfileGateway).should().findActiveProviderPublicProfileById(providerId);
    }

    @Test
    void shouldThrowNotFoundWhenProviderDoesNotExist() {
        UUID providerId = UUID.randomUUID();
        given(providerProfileGateway.findActiveProviderPublicProfileById(providerId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> getProviderProfileUseCase.execute(new GetProviderProfileCommand(providerId)))
                .isInstanceOf(ProviderNotFoundException.class);
    }
}
