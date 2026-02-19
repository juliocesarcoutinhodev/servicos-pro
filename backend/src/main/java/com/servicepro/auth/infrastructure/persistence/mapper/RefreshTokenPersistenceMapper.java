package com.servicepro.auth.infrastructure.persistence.mapper;

import com.servicepro.auth.domain.model.RefreshToken;
import com.servicepro.auth.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import com.servicepro.shared.infrastructure.mapping.MapStructCentralConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructCentralConfig.class)
public interface RefreshTokenPersistenceMapper {

    default RefreshToken toDomain(RefreshTokenJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return RefreshToken.restore(
                entity.getId(),
                entity.getUserId(),
                entity.getTokenHash(),
                entity.getExpiresAt(),
                entity.isRevoked(),
                entity.getCreatedAt()
        );
    }

    RefreshTokenJpaEntity toJpaEntity(RefreshToken refreshToken);
}
