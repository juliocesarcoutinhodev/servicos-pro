package com.servicepro.catalog.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateServiceCategoryRequest(
        @NotBlank(message = "Nome da categoria e obrigatorio.")
        @Size(max = 120, message = "Nome da categoria deve ter no maximo 120 caracteres.")
        String name,

        @Size(max = 500, message = "Descricao da categoria deve ter no maximo 500 caracteres.")
        String description
) {
}
