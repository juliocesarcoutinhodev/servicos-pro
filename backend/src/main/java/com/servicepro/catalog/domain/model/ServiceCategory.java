package com.servicepro.catalog.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public final class ServiceCategory {

    private final UUID id;
    private final String name;
    private final String slug;
    private final String icon;
    private final String color;
    private final String description;
    private final boolean active;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;

    private ServiceCategory(
            UUID id,
            String name,
            String slug,
            String icon,
            String color,
            String description,
            boolean active,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = id;
        this.name = requireName(name);
        this.slug = requireSlug(slug);
        this.icon = normalizeNullable(icon);
        this.color = normalizeNullable(color);
        this.description = normalizeNullable(description);
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ServiceCategory restore(
            UUID id,
            String name,
            String slug,
            String icon,
            String color,
            String description,
            boolean active,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        return new ServiceCategory(id, name, slug, icon, color, description, active, createdAt, updatedAt);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public String getIcon() {
        return icon;
    }

    public String getColor() {
        return color;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    private static String requireName(String value) {
        String normalized = normalizeNullable(value);
        if (normalized == null) {
            throw new IllegalArgumentException("Nome da categoria e obrigatorio.");
        }
        return normalized;
    }

    private static String requireSlug(String value) {
        String normalized = normalizeNullable(value);
        if (normalized == null) {
            throw new IllegalArgumentException("Slug da categoria e obrigatorio.");
        }
        return normalized;
    }

    private static String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isBlank() ? null : normalized;
    }
}
