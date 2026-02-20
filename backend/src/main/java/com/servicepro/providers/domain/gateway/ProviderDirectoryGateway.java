package com.servicepro.providers.domain.gateway;

import com.servicepro.providers.domain.model.ProviderSummary;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProviderDirectoryGateway {

    Page<ProviderSummary> findActiveProvidersWithServices(UUID categoryId, Pageable pageable);
}
