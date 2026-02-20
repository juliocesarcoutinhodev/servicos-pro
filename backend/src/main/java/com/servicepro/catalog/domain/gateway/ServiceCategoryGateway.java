package com.servicepro.catalog.domain.gateway;

import com.servicepro.catalog.domain.model.ServiceCategory;
import java.util.List;
import java.util.UUID;

public interface ServiceCategoryGateway {

    List<ServiceCategory> findAllActive();

    boolean existsActiveById(UUID categoryId);
}
