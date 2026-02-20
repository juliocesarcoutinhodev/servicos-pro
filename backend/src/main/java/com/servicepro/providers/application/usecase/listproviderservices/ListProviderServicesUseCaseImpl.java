package com.servicepro.providers.application.usecase.listproviderservices;

import com.servicepro.providers.domain.gateway.ProviderServiceGateway;
import com.servicepro.providers.domain.model.ProviderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListProviderServicesUseCaseImpl implements ListProviderServicesUseCase {

    private final ProviderServiceGateway providerServiceGateway;

    @Override
    @Transactional(readOnly = true)
    public List<ProviderService> execute(ListProviderServicesCommand command) {
        return providerServiceGateway.findAllByProviderId(command.providerId());
    }
}
