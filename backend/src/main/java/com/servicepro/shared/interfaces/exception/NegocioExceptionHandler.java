package com.servicepro.shared.interfaces.exception;

import com.servicepro.auth.domain.exception.InvalidCredentialsException;
import com.servicepro.auth.domain.exception.RateLimitExceededException;
import com.servicepro.auth.domain.exception.TokenRevokedException;
import com.servicepro.auth.infrastructure.security.AuthRateLimitFilter;
import com.servicepro.shared.domain.exception.ConflitoNegocioException;
import com.servicepro.shared.domain.exception.NegocioException;
import com.servicepro.shared.domain.exception.RecursoNaoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class NegocioExceptionHandler extends GlobalExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidCredentialsException(
            InvalidCredentialsException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, exception.getMessage(), request);
    }

    @ExceptionHandler(TokenRevokedException.class)
    public ResponseEntity<?> handleTokenRevokedException(
            TokenRevokedException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, exception.getMessage(), request);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<?> handleRateLimitExceededException(
            RateLimitExceededException exception,
            HttpServletRequest request
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.RETRY_AFTER, String.valueOf(exception.getRetryAfterSeconds()));
        headers.add(AuthRateLimitFilter.HEADER_RATE_LIMIT_LIMIT, String.valueOf(exception.getLimit()));
        headers.add(AuthRateLimitFilter.HEADER_RATE_LIMIT_REMAINING, String.valueOf(exception.getRemaining()));
        headers.add(AuthRateLimitFilter.HEADER_RATE_LIMIT_RESET, String.valueOf(exception.getRetryAfterSeconds()));

        Map<String, Object> details = Map.of(
                "action", exception.getAction(),
                "limit", exception.getLimit(),
                "remaining", exception.getRemaining(),
                "retryAfterSeconds", exception.getRetryAfterSeconds(),
                "windowSeconds", exception.getWindowSeconds()
        );

        return buildErrorResponse(
                HttpStatus.TOO_MANY_REQUESTS,
                exception.getMessage(),
                request,
                headers,
                details
        );
    }

    @ExceptionHandler(ConflitoNegocioException.class)
    public ResponseEntity<?> handleConflitoNegocioException(
            ConflitoNegocioException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.CONFLICT, exception.getMessage(), request);
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<?> handleRecursoNaoEncontradoException(
            RecursoNaoEncontradoException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<?> handleNegocioException(NegocioException exception, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    }
}
