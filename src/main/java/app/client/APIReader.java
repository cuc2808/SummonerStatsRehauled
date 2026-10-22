package app.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class APIReader {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Gives raw JSON as String
    public String readAPI(String url) {
        try {
            // HttpClient instance
            HttpClient client = HttpClient.newHttpClient();

            // Create a request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();
            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Check the status code and print the response
            if (response.statusCode() != 200) {
                throw new RuntimeException("Get request failed. Status code: " + response.statusCode());
            }
            System.out.println(response.body());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Makes a GET-Request and converts the JSON into a Java object

    public <T> T getWithJackson(String url, Class<T> type, String... fields) {
        try {
            JsonNode node = objectMapper.readTree(new URI(url).toURL());

            for(String field : fields) {
                node = node.get(field);
            }

            return objectMapper.treeToValue(node, type);
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Converts Json String into a java object
    public <T> T convertFromJson(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}

