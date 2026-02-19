package com.servicepro.auth.application.usecase.refresh;

import com.servicepro.auth.application.usecase.login.TokenPair;

public record RefreshResult(TokenPair tokenPair, String refreshToken) {
}
