package com.servicepro.providers.infrastructure.persistence.mapper;

import com.servicepro.providers.domain.model.ProviderService;
import com.servicepro.providers.infrastructure.persistence.entity.ProviderServiceJpaEntity;
import com.servicepro.shared.infrastructure.mapping.MapStructCentralConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructCentralConfig.class)
public interface ProviderServicePersistenceMapper {

    default ProviderService toDomain(ProviderServiceJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return ProviderService.restore(
                entity.getId(),
                entity.getProviderId(),
                entity.getCategoryId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPriceCents(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    ProviderServiceJpaEntity toJpaEntity(ProviderService providerService);
}
