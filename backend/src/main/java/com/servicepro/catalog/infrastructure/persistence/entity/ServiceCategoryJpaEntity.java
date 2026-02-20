package com.servicepro.catalog.infrastructure.persistence.entity;

import com.servicepro.shared.infrastructure.persistence.BaseJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "tb_service_categories",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_tb_service_categories_normalized_name", columnNames = "normalized_name")
        }
)
@NoArgsConstructor
public class ServiceCategoryJpaEntity extends BaseJpaEntity {

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "normalized_name", nullable = false, length = 120)
    private String normalizedName;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private boolean active;
}
