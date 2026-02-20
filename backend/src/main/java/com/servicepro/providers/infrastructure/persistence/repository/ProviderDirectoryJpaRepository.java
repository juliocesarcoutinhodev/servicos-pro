package com.servicepro.providers.infrastructure.persistence.repository;

import com.servicepro.auth.infrastructure.persistence.entity.UserJpaEntity;
import com.servicepro.auth.domain.model.Role;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProviderDirectoryJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    @Query(
            value = """
                    select provider.id as id,
                           provider.name as name,
                           provider.active as active,
                           count(distinct service.id) as serviceCount
                      from UserJpaEntity provider,
                           ProviderServiceJpaEntity service,
                           ServiceCategoryJpaEntity category
                     where service.providerId = provider.id
                       and category.id = service.categoryId
                       and provider.role = :providerRole
                       and provider.active = true
                       and service.active = true
                       and category.active = true
                       and (
                            :categoryId is null
                            or exists (
                                select 1
                                  from ProviderServiceJpaEntity filteredService,
                                       ServiceCategoryJpaEntity filteredCategory
                                 where filteredService.providerId = provider.id
                                   and filteredCategory.id = filteredService.categoryId
                                   and filteredService.active = true
                                   and filteredCategory.active = true
                                   and filteredCategory.id = :categoryId
                            )
                       )
                     group by provider.id, provider.name, provider.active
                     order by lower(provider.name) asc
                    """,
            countQuery = """
                    select count(distinct provider.id)
                      from UserJpaEntity provider,
                           ProviderServiceJpaEntity service,
                           ServiceCategoryJpaEntity category
                     where service.providerId = provider.id
                       and category.id = service.categoryId
                       and provider.role = :providerRole
                       and provider.active = true
                       and service.active = true
                       and category.active = true
                       and (
                            :categoryId is null
                            or exists (
                                select 1
                                  from ProviderServiceJpaEntity filteredService,
                                       ServiceCategoryJpaEntity filteredCategory
                                 where filteredService.providerId = provider.id
                                   and filteredCategory.id = filteredService.categoryId
                                   and filteredService.active = true
                                   and filteredCategory.active = true
                                   and filteredCategory.id = :categoryId
                            )
                       )
                    """
    )
    Page<ProviderSummaryRowProjection> findActiveProvidersWithServices(
            @Param("providerRole") Role providerRole,
            @Param("categoryId") UUID categoryId,
            Pageable pageable
    );

    @Query("""
            select distinct service.providerId as providerId,
                            category.name as categoryName
              from ProviderServiceJpaEntity service,
                   ServiceCategoryJpaEntity category
             where service.active = true
               and category.id = service.categoryId
               and category.active = true
               and service.providerId in :providerIds
             order by service.providerId asc, category.name asc
            """)
    List<ProviderCategoryNameProjection> findCategoryNamesByProviderIds(@Param("providerIds") Collection<UUID> providerIds);

    interface ProviderSummaryRowProjection {
        UUID getId();

        String getName();

        boolean isActive();

        long getServiceCount();
    }

    interface ProviderCategoryNameProjection {
        UUID getProviderId();

        String getCategoryName();
    }
}
