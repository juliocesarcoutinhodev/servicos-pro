package com.servicepro.providers.application.usecase.listproviders;

import java.util.UUID;

public record ListProvidersCommand(
        int page,
        int size,
        UUID categoryId
) {
}
