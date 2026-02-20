package com.servicepro.providers.interfaces.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ProviderServiceResponse(
        UUID id,
        UUID categoryId,
        String name,
        String description,
        long priceCents,
        boolean active,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
