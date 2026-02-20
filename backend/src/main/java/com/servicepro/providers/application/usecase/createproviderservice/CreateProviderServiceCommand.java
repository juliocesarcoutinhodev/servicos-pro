package com.servicepro.providers.application.usecase.createproviderservice;

import java.util.UUID;

public record CreateProviderServiceCommand(
        UUID providerId,
        UUID categoryId,
        String name,
        String description,
        long priceCents
) {
}
