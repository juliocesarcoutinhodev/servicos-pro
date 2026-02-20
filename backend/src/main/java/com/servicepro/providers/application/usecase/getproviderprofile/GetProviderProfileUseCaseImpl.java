package com.servicepro.providers.application.usecase.getproviderprofile;

import com.servicepro.providers.domain.exception.ProviderNotFoundException;
import com.servicepro.providers.domain.gateway.ProviderProfileGateway;
import com.servicepro.providers.domain.model.ProviderPublicProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetProviderProfileUseCaseImpl implements GetProviderProfileUseCase {

    private final ProviderProfileGateway providerProfileGateway;

    @Override
    @Transactional(readOnly = true)
    public ProviderPublicProfile execute(GetProviderProfileCommand command) {
        return providerProfileGateway.findActiveProviderPublicProfileById(command.providerId())
                .orElseThrow(ProviderNotFoundException::new);
    }
}
