package com.servicepro.auth.domain.exception;

import com.servicepro.shared.domain.exception.NegocioException;

public class InvalidUserNameException extends NegocioException {

    public InvalidUserNameException() {
        super("Nome e obrigatorio.");
    }
}
