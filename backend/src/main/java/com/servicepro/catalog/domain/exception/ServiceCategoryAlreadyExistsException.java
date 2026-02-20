package com.servicepro.catalog.domain.exception;

import com.servicepro.shared.domain.exception.ConflitoNegocioException;

public class ServiceCategoryAlreadyExistsException extends ConflitoNegocioException {

    public ServiceCategoryAlreadyExistsException() {
        super("Categoria de servico ja cadastrada.");
    }
}
