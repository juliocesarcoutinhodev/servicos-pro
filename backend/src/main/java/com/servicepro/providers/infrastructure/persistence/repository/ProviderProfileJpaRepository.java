package com.servicepro.providers.infrastructure.persistence.repository;

import com.servicepro.auth.domain.model.Role;
import com.servicepro.auth.infrastructure.persistence.entity.UserJpaEntity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProviderProfileJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    @Query("""
            select provider.id as id,
                   provider.name as name,
                   provider.active as active
              from UserJpaEntity provider
             where provider.id = :providerId
               and provider.role = :providerRole
               and provider.active = true
            """)
    Optional<ProviderBaseProjection> findActiveProviderBaseById(
            @Param("providerId") UUID providerId,
            @Param("providerRole") Role providerRole
    );

    @Query("""
            select avg(review.rating) as averageRating,
                   count(review.id) as totalReviews
              from ProviderReviewJpaEntity review
             where review.providerId = :providerId
               and review.active = true
            """)
    ProviderReviewStatsProjection findReviewStatsByProviderId(@Param("providerId") UUID providerId);

    @Query("""
            select distinct category.name as categoryName
              from ProviderServiceJpaEntity service,
                   ServiceCategoryJpaEntity category
             where service.providerId = :providerId
               and service.active = true
               and category.id = service.categoryId
               and category.active = true
             order by category.name asc
            """)
    List<ProviderCategoryNameProjection> findCategoryNamesByProviderId(@Param("providerId") UUID providerId);

    @Query("""
            select service.id as id,
                   service.name as name,
                   service.priceCents as priceCents,
                   service.description as description
              from ProviderServiceJpaEntity service
             where service.providerId = :providerId
               and service.active = true
             order by lower(service.name) asc
            """)
    List<ProviderPublicServiceProjection> findActiveServicesByProviderId(@Param("providerId") UUID providerId);

    @Query(
            value = """
                    select review.id as id,
                           client.name as clientName,
                           review.rating as rating,
                           review.comment as comment,
                           review.createdAt as createdAt
                      from ProviderReviewJpaEntity review,
                           UserJpaEntity client
                     where review.clientId = client.id
                       and review.providerId = :providerId
                       and review.active = true
                       and client.active = true
                     order by review.createdAt desc
                    """,
            countQuery = """
                    select count(review.id)
                      from ProviderReviewJpaEntity review,
                           UserJpaEntity client
                     where review.clientId = client.id
                       and review.providerId = :providerId
                       and review.active = true
                       and client.active = true
                    """
    )
    Page<ProviderReviewProjection> findActiveReviewsByProviderId(
            @Param("providerId") UUID providerId,
            Pageable pageable
    );

    boolean existsByIdAndRoleAndActiveTrue(UUID providerId, Role role);

    interface ProviderBaseProjection {
        UUID getId();

        String getName();

        boolean isActive();
    }

    interface ProviderReviewStatsProjection {
        Double getAverageRating();

        long getTotalReviews();
    }

    interface ProviderCategoryNameProjection {
        String getCategoryName();
    }

    interface ProviderPublicServiceProjection {
        UUID getId();

        String getName();

        long getPriceCents();

        String getDescription();
    }

    interface ProviderReviewProjection {
        UUID getId();

        String getClientName();

        int getRating();

        String getComment();

        OffsetDateTime getCreatedAt();
    }
}
