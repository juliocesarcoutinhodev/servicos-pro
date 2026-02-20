package com.servicepro.auth.interfaces.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "Email e obrigatorio.")
        @Email(message = "Email invalido.")
        @Size(max = 320, message = "Email deve ter no maximo 320 caracteres.")
        String email,

        @NotBlank(message = "Codigo e obrigatorio.")
        @Pattern(regexp = "\\d{6}", message = "Codigo deve ter 6 digitos numericos.")
        String code,

        @NotBlank(message = "Senha e obrigatoria.")
        @Size(min = 8, max = 72, message = "Senha deve ter entre 8 e 72 caracteres.")
        String newPassword
) {
}
