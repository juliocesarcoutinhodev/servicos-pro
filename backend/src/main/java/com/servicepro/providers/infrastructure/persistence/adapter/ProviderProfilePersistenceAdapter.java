package com.servicepro.providers.infrastructure.persistence.adapter;

import com.servicepro.auth.domain.model.Role;
import com.servicepro.providers.domain.gateway.ProviderProfileGateway;
import com.servicepro.providers.domain.model.ProviderPublicProfile;
import com.servicepro.providers.domain.model.ProviderPublicService;
import com.servicepro.providers.domain.model.ProviderReview;
import com.servicepro.providers.infrastructure.persistence.repository.ProviderProfileJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProviderProfilePersistenceAdapter implements ProviderProfileGateway {

    private final ProviderProfileJpaRepository providerProfileJpaRepository;

    @Override
    public Optional<ProviderPublicProfile> findActiveProviderPublicProfileById(UUID providerId) {
        Optional<ProviderProfileJpaRepository.ProviderBaseProjection> providerBase =
                providerProfileJpaRepository.findActiveProviderBaseById(providerId, Role.PROVIDER);

        if (providerBase.isEmpty()) {
            return Optional.empty();
        }

        ProviderProfileJpaRepository.ProviderReviewStatsProjection reviewStats =
                providerProfileJpaRepository.findReviewStatsByProviderId(providerId);

        List<String> categoryNames = providerProfileJpaRepository.findCategoryNamesByProviderId(providerId).stream()
                .map(ProviderProfileJpaRepository.ProviderCategoryNameProjection::getCategoryName)
                .toList();

        List<ProviderPublicService> services = providerProfileJpaRepository.findActiveServicesByProviderId(providerId).stream()
                .map(this::toPublicService)
                .toList();

        ProviderProfileJpaRepository.ProviderBaseProjection base = providerBase.get();

        return Optional.of(new ProviderPublicProfile(
                base.getId(),
                base.getName(),
                categoryNames,
                null,
                reviewStats == null ? null : reviewStats.getAverageRating(),
                reviewStats == null ? 0 : Math.toIntExact(reviewStats.getTotalReviews()),
                null,
                null,
                base.isActive(),
                services
        ));
    }

    @Override
    public boolean existsActiveProviderById(UUID providerId) {
        return providerProfileJpaRepository.existsByIdAndRoleAndActiveTrue(providerId, Role.PROVIDER);
    }

    @Override
    public Page<ProviderReview> findActiveReviewsByProviderId(UUID providerId, Pageable pageable) {
        return providerProfileJpaRepository.findActiveReviewsByProviderId(providerId, pageable)
                .map(this::toProviderReview);
    }

    private ProviderPublicService toPublicService(ProviderProfileJpaRepository.ProviderPublicServiceProjection projection) {
        return new ProviderPublicService(
                projection.getId(),
                projection.getName(),
                projection.getPriceCents(),
                projection.getDescription()
        );
    }

    private ProviderReview toProviderReview(ProviderProfileJpaRepository.ProviderReviewProjection projection) {
        return new ProviderReview(
                projection.getId(),
                projection.getClientName(),
                projection.getRating(),
                projection.getComment(),
                projection.getCreatedAt()
        );
    }
}
