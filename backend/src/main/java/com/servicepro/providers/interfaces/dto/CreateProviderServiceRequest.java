package com.servicepro.providers.interfaces.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CreateProviderServiceRequest(
        @NotNull(message = "Categoria e obrigatoria.")
        UUID categoryId,

        @NotBlank(message = "Nome do servico e obrigatorio.")
        @Size(max = 120, message = "Nome do servico deve ter no maximo 120 caracteres.")
        String name,

        @Size(max = 1000, message = "Descricao do servico deve ter no maximo 1000 caracteres.")
        String description,

        @NotNull(message = "Preco do servico e obrigatorio.")
        @Min(value = 0, message = "Preco do servico deve ser maior ou igual a zero.")
        Long priceCents
) {
}
