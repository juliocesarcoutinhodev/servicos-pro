package com.servicepro.auth.domain.model;

import com.servicepro.auth.domain.exception.InvalidEmailException;
import com.servicepro.auth.domain.exception.InvalidPhoneException;
import com.servicepro.auth.domain.exception.InvalidSignupPasswordException;
import com.servicepro.auth.domain.exception.InvalidSignupRoleException;
import com.servicepro.auth.domain.exception.InvalidUserNameException;
import com.servicepro.auth.domain.model.valueobject.Email;
import com.servicepro.auth.domain.model.valueobject.Phone;

public record SignupData(
        String name,
        Email email,
        Phone phone,
        String password,
        Role role
) {

    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final int PASSWORD_MAX_LENGTH = 72;

    public SignupData {
        name = normalizeName(name);
        if (email == null) {
            throw new InvalidEmailException();
        }
        if (phone == null) {
            throw new InvalidPhoneException();
        }
        if (password == null || password.isBlank()) {
            throw new InvalidSignupPasswordException();
        }
        if (password.length() < PASSWORD_MIN_LENGTH || password.length() > PASSWORD_MAX_LENGTH) {
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
