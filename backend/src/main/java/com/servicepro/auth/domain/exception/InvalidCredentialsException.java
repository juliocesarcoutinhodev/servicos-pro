package com.servicepro.auth.domain.exception;

import com.servicepro.shared.domain.exception.NegocioException;

public class InvalidCredentialsException extends NegocioException {

    public InvalidCredentialsException() {
        super("Credenciais invalidas.");
    }
}
