package com.servicepro.providers.domain.gateway;

import com.servicepro.providers.domain.model.ProviderService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProviderServiceGateway {

    ProviderService save(ProviderService providerService);

    List<ProviderService> findAllByProviderId(UUID providerId);

    Optional<ProviderService> findByIdAndProviderId(UUID serviceId, UUID providerId);

    boolean deleteByIdAndProviderId(UUID serviceId, UUID providerId);
}
