package com.servicepro.catalog.application.usecase.listcategories;

import com.servicepro.catalog.domain.model.ServiceCategory;
import java.util.List;

public interface ListServiceCategoriesUseCase {

    List<ServiceCategory> execute();
}
