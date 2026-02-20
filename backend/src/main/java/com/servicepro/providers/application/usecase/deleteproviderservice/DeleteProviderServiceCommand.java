package com.servicepro.providers.application.usecase.deleteproviderservice;

import java.util.UUID;

public record DeleteProviderServiceCommand(
        UUID providerId,
        UUID serviceId
) {
}
