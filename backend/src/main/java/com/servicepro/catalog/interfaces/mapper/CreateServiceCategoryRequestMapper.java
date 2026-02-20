package com.servicepro.catalog.interfaces.mapper;

import com.servicepro.catalog.application.usecase.createcategory.CreateServiceCategoryCommand;
import com.servicepro.catalog.interfaces.dto.CreateServiceCategoryRequest;
import com.servicepro.shared.infrastructure.mapping.MapStructCentralConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructCentralConfig.class)
public interface CreateServiceCategoryRequestMapper {

    CreateServiceCategoryCommand toCommand(CreateServiceCategoryRequest request);
}
