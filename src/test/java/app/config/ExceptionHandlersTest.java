package app.config;

import app.exceptions.ApiException;
import app.exceptions.RiotApiException;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class ExceptionHandlersTest {
    private Javalin app;

    @BeforeEach
    void setUp() {
        app = Javalin.create();
        ExceptionHandlers.register(app);

        // Test routes throw known exceptions so we can verify the HTTP response.
        app.get("/limited", ctx -> {
            throw new RiotApiException(HttpStatus.TOO_MANY_REQUESTS, "Please try again later.", 30L);
        });
        app.get("/unauthorized", ctx -> {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        });
        app.start(0);
    }

    @AfterEach
    void tearDown() {
        app.stop();
    }

    @Test
    void returnsJsonAndRetryAfterForRateLimitException() {
        given()
                .port(app.port())
                .when()
                .get("/limited")
                .then()
                .statusCode(HttpStatus.TOO_MANY_REQUESTS.getCode())
                .contentType("application/json")
                .header("Retry-After", equalTo("30"))
                .body("status", equalTo(HttpStatus.TOO_MANY_REQUESTS.getCode()))
                .body("message", equalTo("Please try again later."));
    }

    @Test
    void returnsUnauthorizedWithoutRetryAfterForLoginError() {
        given()
                .port(app.port())
                .when()
                .get("/unauthorized")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.getCode())
                .header("Retry-After", nullValue())
                .body("status", equalTo(HttpStatus.UNAUTHORIZED.getCode()))
                .body("message", equalTo("Unauthorized"));
    }
}
