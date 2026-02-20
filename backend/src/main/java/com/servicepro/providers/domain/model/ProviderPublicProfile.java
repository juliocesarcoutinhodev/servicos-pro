package com.servicepro.providers.domain.model;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record ProviderPublicProfile(
        UUID id,
        String name,
        List<String> categoryNames,
        String bio,
        Double averageRating,
        int totalReviews,
        Integer totalServicesCompleted,
        Integer approvalRate,
        boolean active,
        List<ProviderPublicService> services
) {

    public ProviderPublicProfile {
        Objects.requireNonNull(id, "id e obrigatorio.");
        Objects.requireNonNull(name, "name e obrigatorio.");
        categoryNames = categoryNames == null ? List.of() : List.copyOf(categoryNames);
        services = services == null ? List.of() : List.copyOf(services);

        if (totalReviews < 0) {
            throw new IllegalArgumentException("totalReviews nao pode ser negativo.");
        }
        if (totalServicesCompleted != null && totalServicesCompleted < 0) {
            throw new IllegalArgumentException("totalServicesCompleted nao pode ser negativo.");
        }
        if (approvalRate != null && (approvalRate < 0 || approvalRate > 100)) {
            throw new IllegalArgumentException("approvalRate deve estar entre 0 e 100.");
        }
    }
}
