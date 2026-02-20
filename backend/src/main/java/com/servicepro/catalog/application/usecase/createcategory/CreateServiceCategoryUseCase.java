package com.servicepro.catalog.application.usecase.createcategory;

import com.servicepro.catalog.domain.model.ServiceCategory;

public interface CreateServiceCategoryUseCase {

    ServiceCategory execute(CreateServiceCategoryCommand command);
}
