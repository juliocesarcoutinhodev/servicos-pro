package com.servicepro.providers.application.usecase.getproviderprofile;

import com.servicepro.providers.domain.model.ProviderPublicProfile;

public interface GetProviderProfileUseCase {

    ProviderPublicProfile execute(GetProviderProfileCommand command);
}
