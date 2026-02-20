package com.servicepro.providers.interfaces.mapper;

import com.servicepro.providers.domain.model.ProviderPublicProfile;
import com.servicepro.providers.domain.model.ProviderPublicService;
import com.servicepro.providers.interfaces.dto.ProviderProfileResponse;
import com.servicepro.providers.interfaces.dto.ProviderPublicServiceResponse;
import com.servicepro.shared.infrastructure.mapping.MapStructCentralConfig;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(config = MapStructCentralConfig.class)
public interface ProviderProfileResponseMapper {

    ProviderProfileResponse toResponse(ProviderPublicProfile providerPublicProfile);

    ProviderPublicServiceResponse toPublicServiceResponse(ProviderPublicService providerPublicService);

    List<ProviderPublicServiceResponse> toPublicServiceResponseList(List<ProviderPublicService> providerPublicServices);
}
