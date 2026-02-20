package com.servicepro.providers.application.usecase.deleteproviderservice;

import com.servicepro.providers.domain.exception.ProviderServiceNotFoundException;
import com.servicepro.providers.domain.gateway.ProviderServiceGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteProviderServiceUseCaseImpl implements DeleteProviderServiceUseCase {

    private final ProviderServiceGateway providerServiceGateway;

    @Override
    @Transactional
    public void execute(DeleteProviderServiceCommand command) {
        boolean deleted = providerServiceGateway.deleteByIdAndProviderId(command.serviceId(), command.providerId());
        if (!deleted) {
            throw new ProviderServiceNotFoundException();
        }
    }
}
