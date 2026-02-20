package com.servicepro.providers.domain.model;

import java.util.Objects;
import java.util.UUID;

public record ProviderPublicService(
        UUID id,
        String name,
        long priceCents,
        String description
) {

    public ProviderPublicService {
        Objects.requireNonNull(id, "id e obrigatorio.");
        Objects.requireNonNull(name, "name e obrigatorio.");
        if (priceCents < 0) {
            throw new IllegalArgumentException("priceCents nao pode ser negativo.");
        }
    }
}
