package com.servicepro.providers.infrastructure.persistence.adapter;

import com.servicepro.auth.domain.model.Role;
import com.servicepro.providers.domain.gateway.ProviderDirectoryGateway;
import com.servicepro.providers.domain.model.ProviderSummary;
import com.servicepro.providers.infrastructure.persistence.repository.ProviderDirectoryJpaRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProviderDirectoryPersistenceAdapter implements ProviderDirectoryGateway {

    private final ProviderDirectoryJpaRepository providerDirectoryJpaRepository;

    @Override
    public Page<ProviderSummary> findActiveProvidersWithServices(UUID categoryId, Pageable pageable) {
        Page<ProviderDirectoryJpaRepository.ProviderSummaryRowProjection> providerRows =
                providerDirectoryJpaRepository.findActiveProvidersWithServices(Role.PROVIDER, categoryId, pageable);

        if (providerRows.isEmpty()) {
            return providerRows.map(this::toProviderSummaryWithoutCategories);
        }

        List<UUID> providerIds = providerRows.getContent().stream()
                .map(ProviderDirectoryJpaRepository.ProviderSummaryRowProjection::getId)
                .toList();

        Map<UUID, List<String>> categoryNamesByProviderId = groupCategoryNamesByProviderId(providerIds);

        return providerRows.map(row -> toProviderSummary(row, categoryNamesByProviderId));
    }

    private Map<UUID, List<String>> groupCategoryNamesByProviderId(List<UUID> providerIds) {
        return providerDirectoryJpaRepository.findCategoryNamesByProviderIds(providerIds).stream()
                .collect(Collectors.groupingBy(
                        ProviderDirectoryJpaRepository.ProviderCategoryNameProjection::getProviderId,
                        LinkedHashMap::new,
                        Collectors.mapping(
                                ProviderDirectoryJpaRepository.ProviderCategoryNameProjection::getCategoryName,
                                Collectors.toList()
                        )
                ));
    }

    private ProviderSummary toProviderSummary(
            ProviderDirectoryJpaRepository.ProviderSummaryRowProjection row,
            Map<UUID, List<String>> categoryNamesByProviderId
    ) {
        return new ProviderSummary(
                row.getId(),
                row.getName(),
                categoryNamesByProviderId.getOrDefault(row.getId(), List.of()),
                null,
                0,
                row.isActive(),
                Math.toIntExact(row.getServiceCount())
        );
    }

    private ProviderSummary toProviderSummaryWithoutCategories(
            ProviderDirectoryJpaRepository.ProviderSummaryRowProjection row
    ) {
        return new ProviderSummary(
                row.getId(),
                row.getName(),
                List.of(),
                null,
                0,
                row.isActive(),
                Math.toIntExact(row.getServiceCount())
        );
    }
}
