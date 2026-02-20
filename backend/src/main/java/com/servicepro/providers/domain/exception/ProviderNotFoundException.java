package com.servicepro.providers.domain.exception;

import com.servicepro.shared.domain.exception.RecursoNaoEncontradoException;

public class ProviderNotFoundException extends RecursoNaoEncontradoException {

    public ProviderNotFoundException() {
        super("Prestador nao encontrado ou inativo.");
    }
}
