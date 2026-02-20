package com.servicepro.providers.application.usecase.listproviders;

import com.servicepro.providers.domain.model.ProviderSummary;
import org.springframework.data.domain.Page;

public interface ListProvidersUseCase {

    Page<ProviderSummary> execute(ListProvidersCommand command);
}
