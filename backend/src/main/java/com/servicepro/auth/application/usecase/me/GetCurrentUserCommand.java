package com.servicepro.auth.application.usecase.me;

import java.util.UUID;

public record GetCurrentUserCommand(UUID userId) {
}
