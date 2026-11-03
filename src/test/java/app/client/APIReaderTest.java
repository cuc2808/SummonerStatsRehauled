package app.client;

import app.config.ExceptionHandlers;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class APIReaderTest {
    private Javalin app;

    @BeforeEach
    void setUp() {
        // RiotApiClient loads API_KEY from config.properties, just as in the application.
        RiotApiClient riotApiClient = new RiotApiClient();

        // This test route calls the real Riot API without starting the database.
        app = Javalin.create();
        ExceptionHandlers.register(app);
        app.get("/account", ctx -> ctx.json(
                riotApiClient.getRiotAccountFromGameNameTaglineAndRegion(
                        "europe", ctx.queryParam("gameName"), ctx.queryParam("tagLine"))));
        app.start(0);
    }

    @AfterEach
    void tearDown() {
        if (app != null) {
            app.stop();
        }
    }

    @Test
    void returnsAccountFromRiot() {
        // Change these values if the account changes its Riot ID.
        given()
                .baseUri("http://localhost")
                .port(app.port())
                .queryParam("gameName", "CUC")
                .queryParam("tagLine", "2808")
                .when()
                .get("/account")
                .then()
                .statusCode(HttpStatus.OK.getCode())
                .body("gameName", equalToIgnoringCase("CUC"))
                .body("tagLine", equalTo("2808"))
                .body("puuid", not(emptyOrNullString()));
    }

    @Test
    void returnsNotFoundForUnknownRiotAccount() {
        // A random Riot ID makes a collision with an existing account very unlikely.
        String unknownGameName = UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        given()
                .baseUri("http://localhost")
                .port(app.port())
                .queryParam("gameName", unknownGameName)
                .queryParam("tagLine", "TEST")
                .when()
                .get("/account")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.getCode())
                .body("status", equalTo(HttpStatus.NOT_FOUND.getCode()))
                .body("message", equalTo("The requested account or resource was not found in the external API."));
    }
}
