package com.servicepro.auth.infrastructure.persistence.adapter;

import com.servicepro.auth.domain.gateway.RefreshTokenGateway;
import com.servicepro.auth.domain.model.RefreshToken;
import com.servicepro.auth.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import com.servicepro.auth.infrastructure.persistence.mapper.RefreshTokenPersistenceMapper;
import com.servicepro.auth.infrastructure.persistence.repository.RefreshTokenJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenPersistenceAdapter implements RefreshTokenGateway {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;
    private final RefreshTokenPersistenceMapper refreshTokenPersistenceMapper;

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenJpaEntity savedEntity = refreshTokenJpaRepository.save(
                refreshTokenPersistenceMapper.toJpaEntity(refreshToken)
        );
        return refreshTokenPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return refreshTokenJpaRepository.findByTokenHash(tokenHash)
                .map(refreshTokenPersistenceMapper::toDomain);
    }
}
