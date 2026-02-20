package com.servicepro.providers.domain.gateway;

import com.servicepro.providers.domain.model.ProviderPublicProfile;
import com.servicepro.providers.domain.model.ProviderReview;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProviderProfileGateway {

    Optional<ProviderPublicProfile> findActiveProviderPublicProfileById(UUID providerId);

    boolean existsActiveProviderById(UUID providerId);

    Page<ProviderReview> findActiveReviewsByProviderId(UUID providerId, Pageable pageable);
}
