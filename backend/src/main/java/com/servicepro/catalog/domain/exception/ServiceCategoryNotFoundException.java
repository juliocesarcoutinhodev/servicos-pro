package com.servicepro.catalog.domain.exception;

import com.servicepro.shared.domain.exception.RecursoNaoEncontradoException;

public class ServiceCategoryNotFoundException extends RecursoNaoEncontradoException {

    public ServiceCategoryNotFoundException() {
        super("Categoria de servico nao encontrada ou inativa.");
    }
}
