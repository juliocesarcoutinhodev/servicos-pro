package com.servicepro.providers.infrastructure.persistence.repository;

import com.servicepro.providers.infrastructure.persistence.entity.ProviderServiceJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProviderServiceJpaRepository extends JpaRepository<ProviderServiceJpaEntity, UUID> {

    List<ProviderServiceJpaEntity> findAllByProviderIdOrderByCreatedAtDesc(UUID providerId);

    Optional<ProviderServiceJpaEntity> findByIdAndProviderId(UUID id, UUID providerId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            delete from ProviderServiceJpaEntity service
             where service.id = :serviceId
               and service.providerId = :providerId
            """)
    int deleteByIdAndProviderId(
            @Param("serviceId") UUID serviceId,
            @Param("providerId") UUID providerId
    );
}
