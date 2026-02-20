package com.servicepro.providers.domain.exception;

import com.servicepro.shared.domain.exception.RecursoNaoEncontradoException;

public class ProviderServiceNotFoundException extends RecursoNaoEncontradoException {

    public ProviderServiceNotFoundException() {
        super("Servico do prestador nao encontrado.");
    }
}
