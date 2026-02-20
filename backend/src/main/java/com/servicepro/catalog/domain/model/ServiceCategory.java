package com.servicepro.catalog.domain.model;

import com.servicepro.catalog.domain.exception.InvalidServiceCategoryException;
import java.time.OffsetDateTime;
import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;

public final class ServiceCategory {

    private static final int NAME_MAX_LENGTH = 120;
    private static final int DESCRIPTION_MAX_LENGTH = 500;

    private final UUID id;
    private final String name;
    private final String normalizedName;
    private final String description;
    private final boolean active;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;

    private ServiceCategory(
            UUID id,
            String name,
            String normalizedName,
            String description,
            boolean active,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = id;
        this.name = normalizeName(name);
        this.normalizedName = normalizeStoredNormalizedName(normalizedName, this.name);
        this.description = normalizeDescription(description);
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ServiceCategory create(String name, String description) {
        String normalizedDisplayName = normalizeName(name);
        return new ServiceCategory(
                null,
                normalizedDisplayName,
                normalizeForUniqueness(normalizedDisplayName),
                description,
                true,
                null,
                null
        );
    }

    public static ServiceCategory restore(
            UUID id,
            String name,
            String normalizedName,
            String description,
            boolean active,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        return new ServiceCategory(id, name, normalizedName, description, active, createdAt, updatedAt);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNormalizedName() {
        return normalizedName;
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

    public static String normalizeForUniqueness(String rawName) {
        String normalized = normalizeRawText(rawName);
        if (normalized == null) {
            throw new InvalidServiceCategoryException("Nome da categoria e obrigatorio.");
        }
        normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        if (normalized.isBlank()) {
            throw new InvalidServiceCategoryException("Nome da categoria e obrigatorio.");
        }

        if (normalized.length() > NAME_MAX_LENGTH) {
            throw new InvalidServiceCategoryException("Nome da categoria deve ter no maximo 120 caracteres.");
        }

        return normalized;
    }

    private static String normalizeName(String value) {
        String normalized = normalizeRawText(value);
        if (normalized == null) {
            throw new InvalidServiceCategoryException("Nome da categoria e obrigatorio.");
        }
        if (normalized.length() > NAME_MAX_LENGTH) {
            throw new InvalidServiceCategoryException("Nome da categoria deve ter no maximo 120 caracteres.");
        }
        return normalized;
    }

    private static String normalizeStoredNormalizedName(String value, String fallbackName) {
        String normalized = normalizeRawText(value);
        if (normalized == null) {
            return normalizeForUniqueness(fallbackName);
        }
        if (normalized.length() > NAME_MAX_LENGTH) {
            throw new InvalidServiceCategoryException("Nome normalizado da categoria invalido.");
        }
        return normalized.toLowerCase(Locale.ROOT);
    }

    private static String normalizeDescription(String value) {
        String normalized = normalizeRawText(value);
        if (normalized == null) {
            return null;
        }
        if (normalized.length() > DESCRIPTION_MAX_LENGTH) {
            throw new InvalidServiceCategoryException("Descricao da categoria deve ter no maximo 500 caracteres.");
        }
        return normalized;
    }

    private static String normalizeRawText(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim().replaceAll("\\s+", " ");
        return normalized.isBlank() ? null : normalized;
    }
}
