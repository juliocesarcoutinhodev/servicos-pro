package com.servicepro.catalog.infrastructure.persistence.mapper;

import com.servicepro.catalog.domain.model.ServiceCategory;
import com.servicepro.catalog.infrastructure.persistence.entity.ServiceCategoryJpaEntity;
import com.servicepro.shared.infrastructure.mapping.MapStructCentralConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructCentralConfig.class)
public interface ServiceCategoryPersistenceMapper {

    default ServiceCategory toDomain(ServiceCategoryJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return ServiceCategory.restore(
                entity.getId(),
                entity.getName(),
                entity.getSlug(),
                entity.getIcon(),
                entity.getColor(),
                entity.getDescription(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
