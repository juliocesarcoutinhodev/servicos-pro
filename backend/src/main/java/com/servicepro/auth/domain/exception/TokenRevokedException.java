package com.servicepro.auth.domain.exception;

import com.servicepro.shared.domain.exception.NegocioException;

public class TokenRevokedException extends NegocioException {

    public TokenRevokedException() {
        super("Refresh token invalido, expirado ou revogado.");
    }
}
