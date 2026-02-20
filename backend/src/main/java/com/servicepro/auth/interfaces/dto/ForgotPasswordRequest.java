package com.servicepro.auth.interfaces.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ForgotPasswordRequest(
        @NotBlank(message = "Email e obrigatorio.")
        @Email(message = "Email invalido.")
        @Size(max = 320, message = "Email deve ter no maximo 320 caracteres.")
        String email
) {
}
