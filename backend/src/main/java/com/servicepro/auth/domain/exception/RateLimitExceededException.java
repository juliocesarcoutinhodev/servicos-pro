package com.servicepro.auth.domain.exception;

import com.servicepro.shared.domain.exception.NegocioException;

public class RateLimitExceededException extends NegocioException {

    private final String action;
    private final int limit;
    private final long remaining;
    private final long retryAfterSeconds;
    private final long windowSeconds;

    public RateLimitExceededException(
            String action,
            int limit,
            long remaining,
            long retryAfterSeconds,
            long windowSeconds
    ) {
        super("Limite de requisicoes excedido para " + action + ". Tente novamente em " + retryAfterSeconds + " segundos.");
        this.action = action;
        this.limit = limit;
        this.remaining = remaining;
        this.retryAfterSeconds = retryAfterSeconds;
        this.windowSeconds = windowSeconds;
    }

    public String getAction() {
        return action;
    }

    public int getLimit() {
        return limit;
    }

    public long getRemaining() {
        return remaining;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }

    public long getWindowSeconds() {
        return windowSeconds;
    }
}
