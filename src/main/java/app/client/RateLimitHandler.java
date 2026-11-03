package app.client;

import app.exceptions.RiotApiException;
import io.javalin.http.HttpStatus;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/** Tracks Retry-After cooldowns for the hosts contacted by one APIReader. */
final class RateLimitHandler {
    private final ConcurrentMap<String, Instant> retryAfterByHost = new ConcurrentHashMap<>();

    void checkAllowed(String host) {
        Instant retryAt = retryAfterByHost.get(host);
        Instant now = Instant.now();
        if (retryAt != null && retryAt.isAfter(now)) {
            long seconds = Math.max(1, Duration.between(now, retryAt).toSeconds() + 1);
            throw new RiotApiException(HttpStatus.TOO_MANY_REQUESTS,
                    "Riot API rate limit exceeded. Please try again later.", seconds);
        }
    }

    Long recordRetryAfter(String host, String header) {
        Long seconds = parseSeconds(header);
        if (seconds == null) {
            return null;
        }

        Instant until;
        try {
            until = Instant.now().plusSeconds(seconds);
        } catch (DateTimeException | ArithmeticException e) {
            until = Instant.MAX;
        }
        retryAfterByHost.merge(host, until, (current, next) -> current.isAfter(next) ? current : next);
        return seconds;
    }

    private Long parseSeconds(String header) {
        if (header == null) {
            return null;
        }
        try {
            long seconds = Long.parseLong(header);
            return seconds >= 0 ? seconds : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
