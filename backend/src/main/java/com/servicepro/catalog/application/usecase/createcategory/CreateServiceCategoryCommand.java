package com.servicepro.catalog.application.usecase.createcategory;

public record CreateServiceCategoryCommand(
        String name,
        String description
) {
}
