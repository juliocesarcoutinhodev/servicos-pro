package com.servicepro.providers.interfaces.dto;

import java.util.UUID;

public record ProviderPublicServiceResponse(
        UUID id,
        String name,
        long priceCents,
        String description
) {
}
