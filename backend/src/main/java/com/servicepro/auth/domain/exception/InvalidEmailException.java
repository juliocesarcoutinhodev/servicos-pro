package com.servicepro.auth.domain.exception;

import com.servicepro.shared.domain.exception.NegocioException;

public class InvalidEmailException extends NegocioException {

    public InvalidEmailException() {
        super("Email invalido.");
    }
}
