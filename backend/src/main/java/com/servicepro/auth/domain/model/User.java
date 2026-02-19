package com.servicepro.auth.domain.model;

import com.servicepro.auth.domain.exception.InvalidSignupRoleException;
import com.servicepro.auth.domain.exception.InvalidUserNameException;
import com.servicepro.auth.domain.model.valueobject.Email;
import com.servicepro.auth.domain.model.valueobject.Phone;
import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.Objects;
import java.util.UUID;

public final class User {

    private static final EnumSet<Role> SIGNUP_ALLOWED_ROLES = EnumSet.of(Role.CLIENT, Role.PROVIDER);

    private final UUID id;
    private final String name;
    private final String email;
    private final String phone;
    private final String passwordHash;
    private final Role role;
    private final boolean active;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;

    private User(
            UUID id,
            String name,
            String email,
            String phone,
            String passwordHash,
            Role role,
            boolean active,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = id;
        this.name = normalizeName(name);
        this.email = Email.of(email).value();
        this.phone = Phone.of(phone).value();
        this.passwordHash = requirePasswordHash(passwordHash);
        this.role = Objects.requireNonNull(role, "Perfil e obrigatorio.");
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static User signUp(
            String name,
            Email email,
            Phone phone,
            String passwordHash,
            Role role
    ) {
        if (!SIGNUP_ALLOWED_ROLES.contains(role)) {
            throw new InvalidSignupRoleException();
        }

        return new User(
                null,
                name,
                email.value(),
                phone.value(),
                passwordHash,
                role,
                true,
                null,
                null
        );
    }

    public static User restore(
            UUID id,
            String name,
            String email,
            String phone,
            String passwordHash,
            Role role,
            boolean active,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        return new User(id, name, email, phone, passwordHash, role, active, createdAt, updatedAt);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    private static String normalizeName(String name) {
        if (name == null) {
            throw new InvalidUserNameException();
        }
        String normalized = name.trim();
        if (normalized.isBlank() || normalized.length() > 120) {
            throw new InvalidUserNameException();
        }
        return normalized;
    }

    private static String requirePasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash e obrigatorio.");
        }
        return passwordHash;
    }
}
