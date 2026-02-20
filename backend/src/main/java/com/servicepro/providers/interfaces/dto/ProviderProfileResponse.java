package com.servicepro.providers.interfaces.dto;

import java.util.List;
import java.util.UUID;

public record ProviderProfileResponse(
        UUID id,
        String name,
        List<String> categoryNames,
        String bio,
        Double averageRating,
        int totalReviews,
        Integer totalServicesCompleted,
        Integer approvalRate,
        boolean active,
        List<ProviderPublicServiceResponse> services
) {
}
