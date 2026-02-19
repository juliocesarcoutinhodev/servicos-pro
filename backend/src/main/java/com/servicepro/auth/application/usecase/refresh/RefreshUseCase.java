package com.servicepro.auth.application.usecase.refresh;

public interface RefreshUseCase {

    RefreshResult execute(RefreshCommand command);
}
