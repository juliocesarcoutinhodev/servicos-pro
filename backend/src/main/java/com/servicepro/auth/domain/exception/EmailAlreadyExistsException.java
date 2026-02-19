package com.servicepro.auth.domain.exception;

import com.servicepro.shared.domain.exception.ConflitoNegocioException;

public class EmailAlreadyExistsException extends ConflitoNegocioException {

    public EmailAlreadyExistsException(String email) {
        super("Ja existe um usuario cadastrado com o email: " + email);
    }
}
