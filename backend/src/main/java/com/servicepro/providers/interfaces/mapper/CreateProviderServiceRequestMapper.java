package com.servicepro.providers.interfaces.mapper;

import com.servicepro.providers.application.usecase.createproviderservice.CreateProviderServiceCommand;
import com.servicepro.providers.interfaces.dto.CreateProviderServiceRequest;
import com.servicepro.shared.infrastructure.mapping.MapStructCentralConfig;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructCentralConfig.class)
public interface CreateProviderServiceRequestMapper {

    @Mapping(target = "providerId", source = "providerId")
    CreateProviderServiceCommand toCommand(CreateProviderServiceRequest request, UUID providerId);
}
