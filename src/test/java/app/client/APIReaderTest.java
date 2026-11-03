package app.client;

import app.exceptions.RiotApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class APIReaderTest {
    private HttpServer server;
    private APIReader reader;
    private String url;
    private int status = 200;
    private String body = "{\"data\":{\"name\":\"Ashe\"}}";
    private String retryAfter;
    private final AtomicInteger requests = new AtomicInteger();

    @BeforeEach
    void startServer() throws IOException {
        reader = new APIReader();
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            requests.incrementAndGet();
            if (retryAfter != null) {
                exchange.getResponseHeaders().add("Retry-After", retryAfter);
            }
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(status, bytes.length);
            try (var output = exchange.getResponseBody()) {
                output.write(bytes);
            }
        });
        server.start();
        url = "http://127.0.0.1:" + server.getAddress().getPort() + "/?api_key=secret-test-key";
    }

    @AfterEach
    void stopServer() {
        server.stop(0);
    }

    @Test
    void readsJsonAndNestedFields() {
        assertEquals("Ashe", reader.getWithJackson(url, JsonNode.class, "data").get("name").asText());
    }

    @ParameterizedTest
    @CsvSource({"400,400", "401,502", "403,502", "404,404", "429,429", "500,503", "502,503", "503,503", "504,503", "418,502"})
    void translatesUpstreamErrorsWithoutLeakingBodyOrKey(int upstreamStatus, int expectedStatus) {
        status = upstreamStatus;
        body = "secret-test-key";
        RiotApiException error = assertThrows(RiotApiException.class,
                () -> reader.getWithJackson(url, JsonNode.class));
        assertEquals(expectedStatus, error.getCode());
        assertFalse(error.getMessage().contains("secret-test-key"));
        assertNull(error.getCause());
    }

    @Test
    void stopsFurtherCallsToHostDuringRetryAfter() {
        status = 429;
        retryAfter = "30";
        RiotApiException first = assertThrows(RiotApiException.class, () -> reader.readAPI(url));
        assertEquals(30L, first.getRetryAfterSeconds());
        status = 200;
        RiotApiException next = assertThrows(RiotApiException.class, () -> reader.readAPI(url));
        assertEquals(429, next.getCode());
        assertTrue(next.getRetryAfterSeconds() > 0);
        assertEquals(1, requests.get());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "-1", "9999999999999999999999"})
    void handlesInvalidRetryAfter(String header) {
        status = 429;
        retryAfter = header;
        RiotApiException error = assertThrows(RiotApiException.class, () -> reader.readAPI(url));
        assertEquals(429, error.getCode());
        assertNull(error.getRetryAfterSeconds());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "null", "not-json", "{}", "{\"data\":null}", "{\"data\":{}}"})
    void rejectsInvalidJsonOrMissingFields(String response) {
        body = response;
        assertEquals(502, assertThrows(RiotApiException.class,
                () -> reader.getWithJackson(url, JsonNode.class, "data", "name")).getCode());
    }

    @Test
    void keepsEmptyCollectionsAsSuccessfulResponses() {
        body = "[]";
        assertEquals(0, reader.getWithJackson(url, String[].class).length);
    }

    @Test
    void rejectsJsonThatCannotBeMappedToRequestedType() {
        body = "{\"notAnArray\":true}";
        assertEquals(502, assertThrows(RiotApiException.class,
                () -> reader.getWithJackson(url, String[].class)).getCode());
    }

    @Test
    void connectionFailureIsReportedAsBadGateway() {
        server.stop(0);
        assertEquals(502, assertThrows(RiotApiException.class, () -> reader.readAPI(url)).getCode());
    }

    @Test
    void timeoutIsReportedAsGatewayTimeout() {
        // A socket accepting no connections keeps the request waiting without sleeping in the test.
        try (var socket = new java.net.ServerSocket(0, 1, java.net.InetAddress.getLoopbackAddress())) {
            APIReader shortTimeout = new APIReader(HttpClient.newHttpClient(), Duration.ofMillis(100));
            String slowUrl = "http://localhost:" + socket.getLocalPort();
            assertEquals(504, assertThrows(RiotApiException.class, () -> shortTimeout.readAPI(slowUrl)).getCode());
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    @Test
    void interruptionPreservesInterruptFlag() {
        Thread.currentThread().interrupt();
        try {
            assertEquals(503, assertThrows(RiotApiException.class, () -> reader.readAPI(url)).getCode());
            assertTrue(Thread.currentThread().isInterrupted());
        } finally {
            Thread.interrupted();
        }
    }

    @Test
    void invalidUrlDoesNotExposeApiKey() {
        RiotApiException error = assertThrows(RiotApiException.class,
                () -> reader.readAPI("invalid url?api_key=secret-test-key"));
        assertEquals(500, error.getCode());
        assertFalse(error.getMessage().contains("secret-test-key"));
    }
}
