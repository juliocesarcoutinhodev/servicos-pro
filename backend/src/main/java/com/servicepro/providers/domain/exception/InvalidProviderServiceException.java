package com.servicepro.providers.domain.exception;

import com.servicepro.shared.domain.exception.NegocioException;

public class InvalidProviderServiceException extends NegocioException {

    public InvalidProviderServiceException(String message) {
        super(message);
    }
}
