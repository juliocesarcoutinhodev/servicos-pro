package com.servicepro.catalog.infrastructure.persistence.repository;

import com.servicepro.catalog.infrastructure.persistence.entity.ServiceCategoryJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceCategoryJpaRepository extends JpaRepository<ServiceCategoryJpaEntity, UUID> {

    List<ServiceCategoryJpaEntity> findAllByActiveTrueOrderByNameAsc();

    boolean existsByIdAndActiveTrue(UUID id);

    boolean existsByNormalizedName(String normalizedName);
}
