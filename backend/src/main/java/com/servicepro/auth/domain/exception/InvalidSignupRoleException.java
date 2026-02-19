package com.servicepro.auth.domain.exception;

import com.servicepro.shared.domain.exception.NegocioException;

public class InvalidSignupRoleException extends NegocioException {

    public InvalidSignupRoleException() {
        super("Perfil de cadastro invalido. Apenas CLIENT e PROVIDER sao permitidos.");
    }
}
