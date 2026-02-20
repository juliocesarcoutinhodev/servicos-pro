package com.servicepro.providers.application.usecase.updateproviderservice;

import com.servicepro.providers.domain.model.ProviderService;

public interface UpdateProviderServiceUseCase {

    ProviderService execute(UpdateProviderServiceCommand command);
}
