package com.servicepro.providers.application.usecase.updateproviderservice;

import com.servicepro.catalog.domain.exception.ServiceCategoryNotFoundException;
import com.servicepro.catalog.domain.gateway.ServiceCategoryGateway;
import com.servicepro.providers.domain.exception.ProviderServiceNotFoundException;
import com.servicepro.providers.domain.gateway.ProviderServiceGateway;
import com.servicepro.providers.domain.model.ProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateProviderServiceUseCaseImpl implements UpdateProviderServiceUseCase {

    private final ProviderServiceGateway providerServiceGateway;
    private final ServiceCategoryGateway serviceCategoryGateway;

    @Override
    @Transactional
    public ProviderService execute(UpdateProviderServiceCommand command) {
        ProviderService existingService = providerServiceGateway.findByIdAndProviderId(
                command.serviceId(),
                command.providerId()
        ).orElseThrow(ProviderServiceNotFoundException::new);

        if (!serviceCategoryGateway.existsActiveById(command.categoryId())) {
            throw new ServiceCategoryNotFoundException();
        }

        ProviderService updatedService = existingService.update(
                command.categoryId(),
                command.name(),
                command.description(),
                command.priceCents()
        );
        return providerServiceGateway.save(updatedService);
    }
}
