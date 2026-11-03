package app.exceptions;

import io.javalin.http.HttpStatus;

/** A safe error message and HTTP status for failures in Riot/Data Dragon requests. */
public class RiotApiException extends ApiException {
    private final Long retryAfterSeconds;

    public RiotApiException(HttpStatus status, String message) {
        this(status, message, null);
    }

    public RiotApiException(HttpStatus status, String message, Long retryAfterSeconds) {
        super(status, message);
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public Long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
