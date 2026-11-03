package app.config;

import app.exceptions.ApiException;
import app.exceptions.RiotApiException;
import io.javalin.Javalin;

public final class ExceptionHandlers {
    private ExceptionHandlers() {
    }

    public static void register(Javalin app) {
        app.exception(ApiException.class, (exception, ctx) -> {
            if (exception instanceof RiotApiException riotException && riotException.getRetryAfterSeconds() != null) {
                ctx.header("Retry-After", riotException.getRetryAfterSeconds().toString());
            }
            ctx.status(exception.getStatus()).json(new ErrorResponse(exception.getCode(), exception.getMessage()));
        });
    }

    public record ErrorResponse(int status, String message) {
    }
}
