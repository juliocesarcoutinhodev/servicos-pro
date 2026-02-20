package com.servicepro.catalog.domain.gateway;

import com.servicepro.catalog.domain.model.ServiceCategory;
import java.util.List;
import java.util.UUID;

public interface ServiceCategoryGateway {

    ServiceCategory save(ServiceCategory serviceCategory);

    List<ServiceCategory> findAllActive();

    boolean existsActiveById(UUID categoryId);

    boolean existsByNormalizedName(String normalizedName);
}
