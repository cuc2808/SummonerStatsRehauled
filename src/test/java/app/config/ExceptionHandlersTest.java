package app.config;

import io.javalin.http.HttpStatus;
import app.exceptions.ApiException;
import app.exceptions.RiotApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionHandlersTest {
    @Test
    void returnsJsonStatusAndRetryAfterWithoutStartingDatabase() throws Exception {
        Javalin app = Javalin.create();
        ExceptionHandlers.register(app);
        app.get("/limited", ctx -> {
            throw new RiotApiException(HttpStatus.TOO_MANY_REQUESTS, "Please try again later.", 30L);
        });
        app.get("/unauthorized", ctx -> {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        });
        try {
            app.start(0);
            try (HttpClient client = HttpClient.newHttpClient()) {
                var response = client.send(HttpRequest.newBuilder(URI.create("http://localhost:" + app.port() + "/limited")).build(),
                        HttpResponse.BodyHandlers.ofString());
                assertEquals(429, response.statusCode());
                assertEquals("30", response.headers().firstValue("Retry-After").orElseThrow());
                var json = new ObjectMapper().readTree(response.body());
                assertEquals(429, json.get("status").asInt());
                assertEquals("Please try again later.", json.get("message").asText());

                var unauthorized = client.send(HttpRequest.newBuilder(URI.create("http://localhost:" + app.port() + "/unauthorized")).build(),
                        HttpResponse.BodyHandlers.ofString());
                assertEquals(401, unauthorized.statusCode());
                assertTrue(unauthorized.headers().firstValue("Retry-After").isEmpty());
            }
        } finally {
            app.stop();
        }
    }
}
