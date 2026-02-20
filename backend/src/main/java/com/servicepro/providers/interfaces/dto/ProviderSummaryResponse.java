package com.servicepro.providers.interfaces.dto;

import java.util.List;
import java.util.UUID;

public record ProviderSummaryResponse(
        UUID id,
        String name,
        List<String> categoryNames,
        Double averageRating,
        int totalReviews,
        boolean active,
        int serviceCount
) {
}
