package app.routes;

import app.config.ExceptionHandlers;
import app.config.HibernateConfig;
import app.controllers.AccountController;
import app.entities.MatchPreview;
import app.entities.account.AccountEntries;
import app.entities.account.AccountIdentity;
import app.entities.account.AccountState;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.javalin.http.HttpStatus;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class AccountRoutesTest {
    private static Javalin app;
    private static EntityManagerFactory previousEmf;
    private static EntityManagerFactory testEmf;
    private static final PostgreSQLContainer database = new PostgreSQLContainer("postgres:16.2");

    @BeforeAll
    static void setUp() {
        // Use a separate database. ApplicationConfig would recreate the configured application tables.
        previousEmf = HibernateConfig.emf;
        database.start();
        testEmf = new Configuration()
                .addAnnotatedClass(AccountIdentity.class)
                .addAnnotatedClass(AccountState.class)
                .addAnnotatedClass(AccountEntries.class)
                .addAnnotatedClass(MatchPreview.class)
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setProperty("hibernate.connection.url", database.getJdbcUrl())
                .setProperty("hibernate.connection.username", database.getUsername())
                .setProperty("hibernate.connection.password", database.getPassword())
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .buildSessionFactory();
        HibernateConfig.emf = testEmf;

        AccountRoutes accountRoutes = new AccountRoutes(new AccountController());
        app = Javalin.create(config -> config.router.apiBuilder(accountRoutes.getAccountRoutes()));
        ExceptionHandlers.register(app);
        app.start(0);

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = app.port();
    }

    @AfterAll
    static void tearDown() {
        if (app != null) {
            app.stop();
        }
        if (testEmf != null) {
            testEmf.close();
        }
        HibernateConfig.emf = previousEmf;
        database.stop();
        RestAssured.reset();
    }

    @Test
    void getAccountRoutesTest() {
        given()
                .when()
                .get("/account/test")
                .then()
                .statusCode(HttpStatus.OK.getCode())
                .body("msg", equalTo("Hello from Open"));
    }

    @Test
    void getAccountRoutesFind() {
        given()
                .queryParam("gameName", "CUC")
                .queryParam("tagLine", "2808")
                .queryParam("region", "eun1")
                .when()
                .get("/account/find")
                .then()
                .statusCode(HttpStatus.OK.getCode())
                .body("accountIdentity.gameName", equalToIgnoringCase("CUC"))
                .body("accountIdentity.puuid", not(emptyOrNullString()));
    }

    @Test
    void getAccountRoutesFindMissingGameName() {
        given()
                .queryParam("tagLine", "TEST")
                .queryParam("region", "eun1")
                .when()
                .get("/account/find")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.getCode())
                .body("status", equalTo(HttpStatus.BAD_REQUEST.getCode()))
                .body("message", equalTo("gameName is required."));
    }

    @Test
    void postAccountRoutesUpdateMalformedJson() {
        given()
                .contentType(ContentType.JSON)
                .body("{invalid")
                .when()
                .post("/account/update")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.getCode())
                .body("status", equalTo(HttpStatus.BAD_REQUEST.getCode()))
                .body("message", equalTo("Request body must be a valid JSON object."));
    }

    @Test
    void getAccountRoutesFindUnknownAccount() {
        String gameName = UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        given()
                .queryParam("gameName", gameName)
                .queryParam("tagLine", "TEST")
                .queryParam("region", "eun1")
                .when()
                .get("/account/find")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.getCode())
                .body("message", equalTo("The requested account or resource was not found in the external API."));
    }
}
