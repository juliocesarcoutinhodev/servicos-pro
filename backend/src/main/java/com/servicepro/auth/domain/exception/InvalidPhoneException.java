package com.servicepro.auth.domain.exception;

import com.servicepro.shared.domain.exception.NegocioException;

public class InvalidPhoneException extends NegocioException {

    public InvalidPhoneException() {
        super("Telefone deve estar no formato E.164.");
    }
}
