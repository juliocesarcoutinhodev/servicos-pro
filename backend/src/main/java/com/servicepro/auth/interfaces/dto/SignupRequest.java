package com.servicepro.auth.interfaces.dto;

import com.servicepro.auth.domain.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank(message = "Nome e obrigatorio.")
        @Size(max = 120, message = "Nome deve ter no maximo 120 caracteres.")
        String name,

        @NotBlank(message = "Email e obrigatorio.")
        @Email(message = "Email invalido.")
        @Size(max = 320, message = "Email deve ter no maximo 320 caracteres.")
        String email,

        @NotBlank(message = "Telefone e obrigatorio.")
        @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Telefone deve estar no formato E.164.")
        String phone,

        @NotBlank(message = "Senha e obrigatoria.")
        @Size(min = 8, max = 72, message = "Senha deve ter entre 8 e 72 caracteres.")
        String password,

        @NotNull(message = "Perfil e obrigatorio.")
        Role role
) {
}
