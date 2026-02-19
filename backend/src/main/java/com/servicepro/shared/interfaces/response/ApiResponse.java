package com.servicepro.shared.interfaces.response;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.http.HttpStatus;

public record ApiResponse<T>(
        OffsetDateTime timestamp,
        int status,
        String message,
        T data
) {

    public static <T> ApiResponse<T> of(HttpStatus status, String message, T data) {
        return new ApiResponse<>(OffsetDateTime.now(ZoneOffset.UTC), status.value(), message, data);
    }
}
