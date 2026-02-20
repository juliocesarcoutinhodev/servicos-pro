package com.servicepro.providers.application.usecase.listproviderservices;

import com.servicepro.providers.domain.model.ProviderService;
import java.util.List;

public interface ListProviderServicesUseCase {

    List<ProviderService> execute(ListProviderServicesCommand command);
}
