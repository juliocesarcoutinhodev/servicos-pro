package com.servicepro.providers.domain.model;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public record ProviderReview(
        UUID id,
        String clientName,
        int rating,
        String comment,
        OffsetDateTime createdAt
) {

    public ProviderReview {
        Objects.requireNonNull(id, "id e obrigatorio.");
        Objects.requireNonNull(clientName, "clientName e obrigatorio.");
        Objects.requireNonNull(createdAt, "createdAt e obrigatorio.");

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("rating deve estar entre 1 e 5.");
        }
    }
}
