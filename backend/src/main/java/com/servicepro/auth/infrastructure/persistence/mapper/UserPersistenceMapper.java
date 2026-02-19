package com.servicepro.auth.infrastructure.persistence.mapper;

import com.servicepro.auth.domain.model.User;
import com.servicepro.auth.infrastructure.persistence.entity.UserJpaEntity;
import com.servicepro.shared.infrastructure.mapping.MapStructCentralConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructCentralConfig.class)
public interface UserPersistenceMapper {

    default User toDomain(UserJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return User.restore(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getPasswordHash(),
                entity.getRole(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    UserJpaEntity toJpaEntity(User user);
}
