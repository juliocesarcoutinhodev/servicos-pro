package com.servicepro.providers.application.usecase.createproviderservice;

import com.servicepro.providers.domain.model.ProviderService;

public interface CreateProviderServiceUseCase {

    ProviderService execute(CreateProviderServiceCommand command);
}
