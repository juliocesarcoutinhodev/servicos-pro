package com.servicepro.providers.infrastructure.persistence.adapter;

import com.servicepro.providers.domain.gateway.ProviderServiceGateway;
import com.servicepro.providers.domain.model.ProviderService;
import com.servicepro.providers.domain.exception.ProviderServiceNotFoundException;
import com.servicepro.providers.infrastructure.persistence.entity.ProviderServiceJpaEntity;
import com.servicepro.providers.infrastructure.persistence.mapper.ProviderServicePersistenceMapper;
import com.servicepro.providers.infrastructure.persistence.repository.ProviderServiceJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProviderServicePersistenceAdapter implements ProviderServiceGateway {

    private final ProviderServiceJpaRepository providerServiceJpaRepository;
    private final ProviderServicePersistenceMapper providerServicePersistenceMapper;

    @Override
    public ProviderService save(ProviderService providerService) {
        ProviderServiceJpaEntity entityToSave;
        if (providerService.getId() == null) {
            entityToSave = providerServicePersistenceMapper.toJpaEntity(providerService);
        } else {
            ProviderServiceJpaEntity existingEntity = providerServiceJpaRepository.findByIdAndProviderId(
                    providerService.getId(),
                    providerService.getProviderId()
            ).orElseThrow(ProviderServiceNotFoundException::new);

            existingEntity.setCategoryId(providerService.getCategoryId());
            existingEntity.setName(providerService.getName());
            existingEntity.setDescription(providerService.getDescription());
            existingEntity.setPriceCents(providerService.getPriceCents());
            existingEntity.setActive(providerService.isActive());
            entityToSave = existingEntity;
        }

        ProviderServiceJpaEntity savedEntity = providerServiceJpaRepository.save(entityToSave);
        return providerServicePersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public List<ProviderService> findAllByProviderId(UUID providerId) {
        return providerServiceJpaRepository.findAllByProviderIdOrderByCreatedAtDesc(providerId).stream()
                .map(providerServicePersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<ProviderService> findByIdAndProviderId(UUID serviceId, UUID providerId) {
        return providerServiceJpaRepository.findByIdAndProviderId(serviceId, providerId)
                .map(providerServicePersistenceMapper::toDomain);
    }

    @Override
    public boolean deleteByIdAndProviderId(UUID serviceId, UUID providerId) {
        return providerServiceJpaRepository.deleteByIdAndProviderId(serviceId, providerId) > 0;
    }
}
