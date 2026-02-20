package com.servicepro.providers.application.usecase.updateproviderservice;

import java.util.UUID;

public record UpdateProviderServiceCommand(
        UUID providerId,
        UUID serviceId,
        UUID categoryId,
        String name,
        String description,
        long priceCents
) {
}
