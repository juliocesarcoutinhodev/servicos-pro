package com.servicepro.providers.interfaces.mapper;

import com.servicepro.providers.application.usecase.updateproviderservice.UpdateProviderServiceCommand;
import com.servicepro.providers.interfaces.dto.UpdateProviderServiceRequest;
import com.servicepro.shared.infrastructure.mapping.MapStructCentralConfig;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructCentralConfig.class)
public interface UpdateProviderServiceRequestMapper {

    @Mapping(target = "providerId", source = "providerId")
    @Mapping(target = "serviceId", source = "serviceId")
    UpdateProviderServiceCommand toCommand(
            UpdateProviderServiceRequest request,
            UUID providerId,
            UUID serviceId
    );
}
