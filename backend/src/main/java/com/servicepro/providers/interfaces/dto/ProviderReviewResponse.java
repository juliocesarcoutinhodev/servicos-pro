package com.servicepro.providers.interfaces.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ProviderReviewResponse(
        UUID id,
        String clientName,
        int rating,
        String comment,
        OffsetDateTime createdAt
) {
}
