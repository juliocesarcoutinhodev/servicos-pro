package com.servicepro.auth.interfaces.dto;

import com.servicepro.auth.domain.model.Role;
import java.time.OffsetDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        String phone,
        Role role,
        OffsetDateTime createdAt,
        boolean active
) {
}
