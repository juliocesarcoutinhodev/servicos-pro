package com.servicepro.auth.infrastructure.persistence.adapter;

import com.servicepro.auth.domain.gateway.PasswordResetTokenGateway;
import com.servicepro.auth.domain.model.PasswordResetToken;
import com.servicepro.auth.infrastructure.persistence.entity.PasswordResetTokenJpaEntity;
import com.servicepro.auth.infrastructure.persistence.mapper.PasswordResetTokenPersistenceMapper;
import com.servicepro.auth.infrastructure.persistence.repository.PasswordResetTokenJpaRepository;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordResetTokenPersistenceAdapter implements PasswordResetTokenGateway {

    private final PasswordResetTokenJpaRepository repository;
    private final PasswordResetTokenPersistenceMapper mapper;

    @Override
    public PasswordResetToken save(PasswordResetToken token) {
        PasswordResetTokenJpaEntity savedEntity = repository.save(mapper.toJpaEntity(token));
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<PasswordResetToken> findLatestActiveByUserIdAndTokenHash(UUID userId, String tokenHash) {
        return repository.findTopByUserIdAndTokenHashAndUsedAtIsNullOrderByCreatedAtDesc(userId, tokenHash)
                .map(mapper::toDomain);
    }

    @Override
    public void markAllAsUsedByUserId(UUID userId, OffsetDateTime usedAt) {
        repository.markAllAsUsedByUserId(userId, usedAt);
    }
}
