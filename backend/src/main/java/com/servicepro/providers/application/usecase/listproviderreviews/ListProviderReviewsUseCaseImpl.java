package com.servicepro.providers.application.usecase.listproviderreviews;

import com.servicepro.providers.domain.exception.ProviderNotFoundException;
import com.servicepro.providers.domain.gateway.ProviderProfileGateway;
import com.servicepro.providers.domain.model.ProviderReview;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListProviderReviewsUseCaseImpl implements ListProviderReviewsUseCase {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 100;

    private final ProviderProfileGateway providerProfileGateway;

    @Override
    @Transactional(readOnly = true)
    public Page<ProviderReview> execute(ListProviderReviewsCommand command) {
        if (!providerProfileGateway.existsActiveProviderById(command.providerId())) {
            throw new ProviderNotFoundException();
        }

        int normalizedPage = Math.max(DEFAULT_PAGE, command.page());
        int normalizedSize = command.size() <= 0
                ? DEFAULT_SIZE
                : Math.min(command.size(), MAX_SIZE);

        Pageable pageable = PageRequest.of(normalizedPage, normalizedSize);
        return providerProfileGateway.findActiveReviewsByProviderId(command.providerId(), pageable);
    }
}
