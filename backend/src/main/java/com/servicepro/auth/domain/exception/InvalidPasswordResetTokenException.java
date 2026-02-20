package com.servicepro.auth.domain.exception;

import com.servicepro.shared.domain.exception.NegocioException;

public class InvalidPasswordResetTokenException extends NegocioException {

    public InvalidPasswordResetTokenException() {
        super("Codigo de redefinicao de senha invalido ou expirado.");
    }
}
