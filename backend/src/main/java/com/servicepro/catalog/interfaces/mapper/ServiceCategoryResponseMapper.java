package com.servicepro.catalog.interfaces.mapper;

import com.servicepro.catalog.domain.model.ServiceCategory;
import com.servicepro.catalog.interfaces.dto.ServiceCategoryResponse;
import com.servicepro.shared.infrastructure.mapping.MapStructCentralConfig;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(config = MapStructCentralConfig.class)
public interface ServiceCategoryResponseMapper {

    ServiceCategoryResponse toResponse(ServiceCategory category);

    List<ServiceCategoryResponse> toResponseList(List<ServiceCategory> categories);
}
