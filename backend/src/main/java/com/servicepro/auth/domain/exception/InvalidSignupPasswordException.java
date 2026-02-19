package com.servicepro.auth.domain.exception;

import com.servicepro.shared.domain.exception.NegocioException;

public class InvalidSignupPasswordException extends NegocioException {

    public InvalidSignupPasswordException() {
        super("Senha e obrigatoria.");
    }
}
