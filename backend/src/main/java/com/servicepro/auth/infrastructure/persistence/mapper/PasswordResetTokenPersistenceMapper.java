package com.servicepro.auth.infrastructure.persistence.mapper;

import com.servicepro.auth.domain.model.PasswordResetToken;
import com.servicepro.auth.infrastructure.persistence.entity.PasswordResetTokenJpaEntity;
import com.servicepro.shared.infrastructure.mapping.MapStructCentralConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructCentralConfig.class)
public interface PasswordResetTokenPersistenceMapper {

    default PasswordResetToken toDomain(PasswordResetTokenJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return PasswordResetToken.restore(
                entity.getId(),
                entity.getUserId(),
                entity.getTokenHash(),
                entity.getExpiresAt(),
                entity.getUsedAt(),
                entity.getCreatedAt()
        );
    }

    PasswordResetTokenJpaEntity toJpaEntity(PasswordResetToken token);
}
