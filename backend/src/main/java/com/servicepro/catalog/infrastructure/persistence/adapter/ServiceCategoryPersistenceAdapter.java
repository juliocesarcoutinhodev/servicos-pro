package com.servicepro.catalog.infrastructure.persistence.adapter;

import com.servicepro.catalog.domain.gateway.ServiceCategoryGateway;
import com.servicepro.catalog.domain.model.ServiceCategory;
import com.servicepro.catalog.infrastructure.persistence.mapper.ServiceCategoryPersistenceMapper;
import com.servicepro.catalog.infrastructure.persistence.repository.ServiceCategoryJpaRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServiceCategoryPersistenceAdapter implements ServiceCategoryGateway {

    private final ServiceCategoryJpaRepository serviceCategoryJpaRepository;
    private final ServiceCategoryPersistenceMapper serviceCategoryPersistenceMapper;

    @Override
    public ServiceCategory save(ServiceCategory serviceCategory) {
        return serviceCategoryPersistenceMapper.toDomain(
                serviceCategoryJpaRepository.save(serviceCategoryPersistenceMapper.toJpaEntity(serviceCategory))
        );
    }

    @Override
    public List<ServiceCategory> findAllActive() {
        return serviceCategoryJpaRepository.findAllByActiveTrueOrderByNameAsc().stream()
                .map(serviceCategoryPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsActiveById(UUID categoryId) {
        return serviceCategoryJpaRepository.existsByIdAndActiveTrue(categoryId);
    }

    @Override
    public boolean existsByNormalizedName(String normalizedName) {
        return serviceCategoryJpaRepository.existsByNormalizedName(normalizedName);
    }
}
