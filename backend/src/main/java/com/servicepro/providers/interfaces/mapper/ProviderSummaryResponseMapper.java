package com.servicepro.providers.interfaces.mapper;

import com.servicepro.providers.domain.model.ProviderSummary;
import com.servicepro.providers.interfaces.dto.ProviderSummaryResponse;
import com.servicepro.shared.infrastructure.mapping.MapStructCentralConfig;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(config = MapStructCentralConfig.class)
public interface ProviderSummaryResponseMapper {

    ProviderSummaryResponse toResponse(ProviderSummary providerSummary);

    List<ProviderSummaryResponse> toResponseList(List<ProviderSummary> providerSummaries);
}
