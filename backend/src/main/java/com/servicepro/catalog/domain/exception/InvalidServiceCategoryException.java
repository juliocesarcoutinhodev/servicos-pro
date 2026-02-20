package com.servicepro.catalog.domain.exception;

import com.servicepro.shared.domain.exception.NegocioException;

public class InvalidServiceCategoryException extends NegocioException {

    public InvalidServiceCategoryException(String message) {
        super(message);
    }
}
