package com.servicepro.providers.interfaces.mapper;

import com.servicepro.providers.domain.model.ProviderService;
import com.servicepro.providers.interfaces.dto.ProviderServiceResponse;
import com.servicepro.shared.infrastructure.mapping.MapStructCentralConfig;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(config = MapStructCentralConfig.class)
public interface ProviderServiceResponseMapper {

    ProviderServiceResponse toResponse(ProviderService providerService);

    List<ProviderServiceResponse> toResponseList(List<ProviderService> providerServices);
}
