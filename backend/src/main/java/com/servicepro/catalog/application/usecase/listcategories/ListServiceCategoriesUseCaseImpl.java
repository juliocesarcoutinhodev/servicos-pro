package com.servicepro.catalog.application.usecase.listcategories;

import com.servicepro.catalog.domain.gateway.ServiceCategoryGateway;
import com.servicepro.catalog.domain.model.ServiceCategory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListServiceCategoriesUseCaseImpl implements ListServiceCategoriesUseCase {

    private final ServiceCategoryGateway serviceCategoryGateway;

    @Override
    @Transactional(readOnly = true)
    public List<ServiceCategory> execute() {
        return serviceCategoryGateway.findAllActive();
    }
}
