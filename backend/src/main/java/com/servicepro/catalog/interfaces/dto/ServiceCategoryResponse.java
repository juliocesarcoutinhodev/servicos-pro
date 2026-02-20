package com.servicepro.catalog.interfaces.dto;

import java.util.UUID;

public record ServiceCategoryResponse(
        UUID id,
        String name,
        String slug,
        String icon,
        String color,
        String description
) {
}
