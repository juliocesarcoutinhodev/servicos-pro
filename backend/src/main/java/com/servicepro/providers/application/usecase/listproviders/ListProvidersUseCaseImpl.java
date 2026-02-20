package com.servicepro.providers.application.usecase.listproviders;

import com.servicepro.providers.domain.gateway.ProviderDirectoryGateway;
import com.servicepro.providers.domain.model.ProviderSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListProvidersUseCaseImpl implements ListProvidersUseCase {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 100;

    private final ProviderDirectoryGateway providerDirectoryGateway;

    @Override
    @Transactional(readOnly = true)
    public Page<ProviderSummary> execute(ListProvidersCommand command) {
        int normalizedPage = Math.max(DEFAULT_PAGE, command.page());
        int normalizedSize = command.size() <= 0
                ? DEFAULT_SIZE
                : Math.min(command.size(), MAX_SIZE);

        Pageable pageable = PageRequest.of(normalizedPage, normalizedSize);
        return providerDirectoryGateway.findActiveProvidersWithServices(command.categoryId(), pageable);
    }
}
