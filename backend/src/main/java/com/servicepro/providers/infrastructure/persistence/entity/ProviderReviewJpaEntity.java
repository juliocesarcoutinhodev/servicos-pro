package com.servicepro.providers.infrastructure.persistence.entity;

import com.servicepro.shared.infrastructure.persistence.BaseJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tb_provider_reviews")
@NoArgsConstructor
public class ProviderReviewJpaEntity extends BaseJpaEntity {

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(nullable = false)
    private short rating;

    @Column(length = 1000)
    private String comment;

    @Column(nullable = false)
    private boolean active;
}
