package com.servicepro.providers.application.usecase.listproviderreviews;

import java.util.UUID;

public record ListProviderReviewsCommand(
        UUID providerId,
        int page,
        int size
) {
}
