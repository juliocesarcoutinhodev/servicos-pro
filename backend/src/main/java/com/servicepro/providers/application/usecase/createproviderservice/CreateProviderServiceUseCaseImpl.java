package com.servicepro.providers.application.usecase.createproviderservice;

import com.servicepro.catalog.domain.exception.ServiceCategoryNotFoundException;
import com.servicepro.catalog.domain.gateway.ServiceCategoryGateway;
import com.servicepro.providers.domain.gateway.ProviderServiceGateway;
import com.servicepro.providers.domain.model.ProviderService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateProviderServiceUseCaseImpl implements CreateProviderServiceUseCase {

    private final ProviderServiceGateway providerServiceGateway;
    private final ServiceCategoryGateway serviceCategoryGateway;

    @Override
    @Transactional
    public ProviderService execute(CreateProviderServiceCommand command) {
        validateCategory(command.categoryId());

        ProviderService providerService = ProviderService.create(
                command.providerId(),
                command.categoryId(),
                command.name(),
                command.description(),
                command.priceCents()
        );

        return providerServiceGateway.save(providerService);
    }

    private void validateCategory(UUID categoryId) {
        if (!serviceCategoryGateway.existsActiveById(categoryId)) {
            throw new ServiceCategoryNotFoundException();
        }
    }
}
