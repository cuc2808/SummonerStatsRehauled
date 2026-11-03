package app.client;

import app.exceptions.RiotApiException;
import io.javalin.http.HttpStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;

public class APIReader {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient client;
    private final Duration requestTimeout;
    private final RateLimitHandler rateLimitHandler = new RateLimitHandler();

    public APIReader() {
        this(HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build(), Duration.ofSeconds(15));
    }

    APIReader(HttpClient client, Duration requestTimeout) {
        this.client = client;
        this.requestTimeout = requestTimeout;
    }

    // Gives raw JSON as String
    public String readAPI(String url) {
        HttpRequest request = createRequest(url);
        String host = request.uri().getAuthority();
        rateLimitHandler.checkAllowed(host);

        HttpResponse<String> response = sendRequest(request);
        if (response.statusCode() != HttpStatus.OK.getCode()) {
            throw responseException(response, host);
        }
        return response.body();
    }

    private HttpRequest createRequest(String url) {
        try {
            return HttpRequest.newBuilder(URI.create(url)).timeout(requestTimeout).GET().build();
        } catch (IllegalArgumentException e) {
            // URLs and original exceptions can contain the API key.
            throw new RiotApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid external API URL.");
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (HttpTimeoutException e) {
            throw new RiotApiException(HttpStatus.GATEWAY_TIMEOUT, "The external API did not respond in time. Please try again later.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RiotApiException(HttpStatus.SERVICE_UNAVAILABLE, "The external API request was interrupted.");
        } catch (IOException e) {
            throw new RiotApiException(HttpStatus.BAD_GATEWAY, "Could not connect to the external API. Please try again later.");
        }
    }

    private RiotApiException responseException(HttpResponse<String> response, String host) {
        return switch (HttpStatus.forStatus(response.statusCode())) {
            case BAD_REQUEST -> new RiotApiException(HttpStatus.BAD_REQUEST, "The external API rejected the request. Check the supplied parameters.");
            // The backend owns these credentials, not the user of our API.
            case UNAUTHORIZED, FORBIDDEN -> new RiotApiException(HttpStatus.BAD_GATEWAY, "Riot API access was denied. The server API key or permissions must be checked.");
            case NOT_FOUND -> new RiotApiException(HttpStatus.NOT_FOUND, "The requested account or resource was not found in the external API.");
            case TOO_MANY_REQUESTS -> {
                String retryAfter = response.headers().firstValue("Retry-After").orElse(null);
                Long seconds = rateLimitHandler.recordRetryAfter(host, retryAfter);
                yield new RiotApiException(HttpStatus.TOO_MANY_REQUESTS, "Riot API rate limit exceeded. Please try again later.", seconds);
            }
            case INTERNAL_SERVER_ERROR, BAD_GATEWAY, SERVICE_UNAVAILABLE, GATEWAY_TIMEOUT -> new RiotApiException(HttpStatus.SERVICE_UNAVAILABLE, "The external API is temporarily unavailable. Please try again later.");
            case null, default -> new RiotApiException(HttpStatus.BAD_GATEWAY, "The external API returned an unexpected HTTP status.");
        };
    }

    // Makes a GET-Request and converts the JSON into a Java object

    public <T> T getWithJackson(String url, Class<T> type, String... fields) {
        try {
            JsonNode node = objectMapper.readTree(readAPI(url));

            for(String field : fields) {
                if (node == null || node.isNull()) {
                    throw invalidResponse();
                }
                node = node.get(field);
            }
            if (node == null || node.isNull() || node.isMissingNode()) {
                throw invalidResponse();
            }
            return objectMapper.treeToValue(node, type);
        } catch (JsonProcessingException e) {
            throw invalidResponse();
        }
    }

    // Converts Json String into a java object
    public <T> T convertFromJson(String json, Class<T> type) {
        try {
            T result = objectMapper.readValue(json, type);
            if (result == null) {
                throw invalidResponse();
            }
            return result;
        } catch (JsonProcessingException e) {
            throw invalidResponse();
        }
    }

    private RiotApiException invalidResponse() {
        return new RiotApiException(HttpStatus.BAD_GATEWAY, "The external API returned an invalid or incomplete JSON response.");
    }
}

