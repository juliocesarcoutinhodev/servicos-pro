package com.servicepro.auth.infrastructure.persistence.repository;

import com.servicepro.auth.infrastructure.persistence.entity.UserJpaEntity;
import java.util.UUID;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    boolean existsByEmailIgnoreCase(String email);

    Optional<UserJpaEntity> findByEmailIgnoreCase(String email);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update UserJpaEntity user
               set user.passwordHash = :passwordHash
             where user.id = :userId
            """)
    int updatePasswordHashById(@Param("userId") UUID userId, @Param("passwordHash") String passwordHash);
}
