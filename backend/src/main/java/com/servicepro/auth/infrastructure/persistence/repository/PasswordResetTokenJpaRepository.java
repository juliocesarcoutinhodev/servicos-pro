package com.servicepro.auth.infrastructure.persistence.repository;

import com.servicepro.auth.infrastructure.persistence.entity.PasswordResetTokenJpaEntity;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface PasswordResetTokenJpaRepository extends JpaRepository<PasswordResetTokenJpaEntity, UUID> {

    Optional<PasswordResetTokenJpaEntity> findTopByUserIdAndTokenHashAndUsedAtIsNullOrderByCreatedAtDesc(
            UUID userId,
            String tokenHash
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update PasswordResetTokenJpaEntity token
               set token.usedAt = :usedAt
             where token.userId = :userId
               and token.usedAt is null
            """)
    int markAllAsUsedByUserId(@Param("userId") UUID userId, @Param("usedAt") OffsetDateTime usedAt);
}
