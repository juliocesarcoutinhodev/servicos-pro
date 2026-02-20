package com.servicepro.providers.domain.model;

import com.servicepro.providers.domain.exception.InvalidProviderServiceException;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public final class ProviderService {

    private static final int NAME_MAX_LENGTH = 120;
    private static final int DESCRIPTION_MAX_LENGTH = 1000;

    private final UUID id;
    private final UUID providerId;
    private final UUID categoryId;
    private final String name;
    private final String description;
    private final long priceCents;
    private final boolean active;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;

    private ProviderService(
            UUID id,
            UUID providerId,
            UUID categoryId,
            String name,
            String description,
            long priceCents,
            boolean active,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = id;
        this.providerId = Objects.requireNonNull(providerId, "providerId e obrigatorio.");
        this.categoryId = Objects.requireNonNull(categoryId, "categoryId e obrigatorio.");
        this.name = validateName(name);
        this.description = validateDescription(description);
        this.priceCents = validatePriceCents(priceCents);
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ProviderService create(
            UUID providerId,
            UUID categoryId,
            String name,
            String description,
            long priceCents
    ) {
        return new ProviderService(
                null,
                providerId,
                categoryId,
                name,
                description,
                priceCents,
                true,
                null,
                null
        );
    }

    public static ProviderService restore(
            UUID id,
            UUID providerId,
            UUID categoryId,
            String name,
            String description,
            long priceCents,
            boolean active,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        return new ProviderService(
                id,
                providerId,
                categoryId,
                name,
                description,
                priceCents,
                active,
                createdAt,
                updatedAt
        );
    }

    public ProviderService update(
            UUID categoryId,
            String name,
            String description,
            long priceCents
    ) {
        return new ProviderService(
                id,
                providerId,
                categoryId,
                name,
                description,
                priceCents,
                active,
                createdAt,
                updatedAt
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getProviderId() {
        return providerId;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public long getPriceCents() {
        return priceCents;
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

    private static String validateName(String value) {
        if (value == null) {
            throw new InvalidProviderServiceException("Nome do servico e obrigatorio.");
        }

        String normalized = value.trim();
        if (normalized.isBlank() || normalized.length() > NAME_MAX_LENGTH) {
            throw new InvalidProviderServiceException("Nome do servico deve ter entre 1 e 120 caracteres.");
        }
        return normalized;
    }

    private static String validateDescription(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();
        if (normalized.isEmpty()) {
            return null;
        }
        if (normalized.length() > DESCRIPTION_MAX_LENGTH) {
            throw new InvalidProviderServiceException("Descricao do servico deve ter no maximo 1000 caracteres.");
        }
        return normalized;
    }

    private static long validatePriceCents(long value) {
        if (value < 0) {
            throw new InvalidProviderServiceException("Preco do servico deve ser maior ou igual a zero.");
        }
        return value;
    }
}
