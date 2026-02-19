package com.servicepro.auth.domain.model;

import com.servicepro.auth.domain.exception.InvalidSignupPasswordException;
import com.servicepro.auth.domain.exception.InvalidSignupRoleException;
import com.servicepro.auth.domain.exception.InvalidUserNameException;
import com.servicepro.auth.domain.model.valueobject.Email;
import com.servicepro.auth.domain.model.valueobject.Phone;
import java.util.Objects;

public record SignupData(
        String name,
        Email email,
        Phone phone,
        String password,
        Role role
) {

    public SignupData {
        name = normalizeName(name);
        email = Objects.requireNonNull(email, "Email e obrigatorio.");
        phone = Objects.requireNonNull(phone, "Telefone e obrigatorio.");
        if (password == null || password.isBlank()) {
            throw new InvalidSignupPasswordException();
        }
        if (role == null) {
            throw new InvalidSignupRoleException();
        }
    }

    private static String normalizeName(String name) {
        if (name == null) {
            throw new InvalidUserNameException();
        }
        String normalized = name.trim();
        if (normalized.isBlank()) {
            throw new InvalidUserNameException();
        }
        return normalized;
    }
}
