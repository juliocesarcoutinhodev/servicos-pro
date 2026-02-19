package com.servicepro.shared.interfaces.exception;

import com.servicepro.shared.interfaces.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class GlobalExceptionHandler {

    protected ResponseEntity<ErrorResponse> buildErrorResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request
    ) {
        return buildErrorResponse(status, message, request, null, null);
    }

    protected ResponseEntity<ErrorResponse> buildErrorResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            HttpHeaders headers,
            Map<String, Object> details
    ) {
        ResponseEntity.BodyBuilder builder = ResponseEntity.status(status);
        if (headers != null) {
            builder.headers(headers);
        }
        return builder
                .body(ErrorResponse.of(status, message, request.getRequestURI(), details));
    }
}
