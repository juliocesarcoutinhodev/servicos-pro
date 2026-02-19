package com.servicepro.auth.domain.model.valueobject;

import com.servicepro.auth.domain.exception.InvalidEmailException;
import java.util.Locale;
import java.util.regex.Pattern;

public record Email(String value) {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public Email {
        String normalized = normalize(value);
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new InvalidEmailException();
        }
        value = normalized;
    }

    public static Email of(String value) {
        return new Email(value);
    }

    private static String normalize(String value) {
        if (value == null) {
            throw new InvalidEmailException();
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        if (normalized.isBlank() || normalized.length() > 320) {
            throw new InvalidEmailException();
        }
        return normalized;
    }
}
