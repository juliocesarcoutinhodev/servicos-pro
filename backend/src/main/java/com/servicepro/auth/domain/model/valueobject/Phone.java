package com.servicepro.auth.domain.model.valueobject;

import com.servicepro.auth.domain.exception.InvalidPhoneException;
import java.util.regex.Pattern;

public record Phone(String value) {

    private static final Pattern E164_PATTERN = Pattern.compile("^\\+[1-9]\\d{1,14}$");

    public Phone {
        String normalized = normalize(value);
        if (!E164_PATTERN.matcher(normalized).matches()) {
            throw new InvalidPhoneException();
        }
        value = normalized;
    }

    public static Phone of(String value) {
        return new Phone(value);
    }

    private static String normalize(String value) {
        if (value == null) {
            throw new InvalidPhoneException();
        }
        String normalized = value.trim();
        if (normalized.isBlank() || normalized.length() > 20) {
            throw new InvalidPhoneException();
        }
        return normalized;
    }
}
