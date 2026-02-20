package com.servicepro.catalog.application.usecase.createcategory;

import com.servicepro.catalog.domain.exception.ServiceCategoryAlreadyExistsException;
import com.servicepro.catalog.domain.gateway.ServiceCategoryGateway;
import com.servicepro.catalog.domain.model.ServiceCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateServiceCategoryUseCaseImpl implements CreateServiceCategoryUseCase {

    private final ServiceCategoryGateway serviceCategoryGateway;

    @Override
    @Transactional
    public ServiceCategory execute(CreateServiceCategoryCommand command) {
        ServiceCategory serviceCategory = ServiceCategory.create(command.name(), command.description());

        if (serviceCategoryGateway.existsByNormalizedName(serviceCategory.getNormalizedName())) {
            throw new ServiceCategoryAlreadyExistsException();
        }

        return serviceCategoryGateway.save(serviceCategory);
    }
}
