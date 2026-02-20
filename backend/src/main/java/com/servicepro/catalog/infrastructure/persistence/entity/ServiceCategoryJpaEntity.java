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
                @UniqueConstraint(name = "uk_tb_service_categories_slug", columnNames = "slug")
        }
)
@NoArgsConstructor
public class ServiceCategoryJpaEntity extends BaseJpaEntity {

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 120)
    private String slug;

    @Column(length = 120)
    private String icon;

    @Column(length = 20)
    private String color;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private boolean active;
}
