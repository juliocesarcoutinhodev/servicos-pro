package com.servicepro.providers.domain.model;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record ProviderSummary(
        UUID id,
        String name,
        List<String> categoryNames,
        Double averageRating,
        int totalReviews,
        boolean active,
        int serviceCount
) {

    public ProviderSummary {
        Objects.requireNonNull(id, "id e obrigatorio.");
        Objects.requireNonNull(name, "name e obrigatorio.");
        categoryNames = categoryNames == null ? List.of() : List.copyOf(categoryNames);

        if (totalReviews < 0) {
            throw new IllegalArgumentException("totalReviews nao pode ser negativo.");
        }
        if (serviceCount < 0) {
            throw new IllegalArgumentException("serviceCount nao pode ser negativo.");
        }
    }
}
