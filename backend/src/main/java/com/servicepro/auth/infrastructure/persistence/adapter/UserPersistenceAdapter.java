package com.servicepro.auth.infrastructure.persistence.adapter;

import com.servicepro.auth.domain.exception.EmailAlreadyExistsException;
import com.servicepro.auth.domain.gateway.UserGateway;
import com.servicepro.auth.domain.model.User;
import com.servicepro.auth.infrastructure.persistence.entity.UserJpaEntity;
import com.servicepro.auth.infrastructure.persistence.mapper.UserPersistenceMapper;
import com.servicepro.auth.infrastructure.persistence.repository.UserJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserGateway {

    private final UserJpaRepository userJpaRepository;
    private final UserPersistenceMapper userPersistenceMapper;

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmailIgnoreCase(email)
                .map(userPersistenceMapper::toDomain);
    }

    @Override
    public User save(User user) {
        try {
            UserJpaEntity savedEntity = userJpaRepository.save(userPersistenceMapper.toJpaEntity(user));
            return userPersistenceMapper.toDomain(savedEntity);
        } catch (DataIntegrityViolationException exception) {
            throw new EmailAlreadyExistsException(user.getEmail());
        }
    }
}
