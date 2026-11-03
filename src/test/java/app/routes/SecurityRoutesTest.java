package app.routes;

import app.config.ExceptionHandlers;
import app.config.HibernateConfig;
import app.daos.UserDAO;
import app.dtos.UserDTO;
import app.entities.Role;
import app.entities.User;
import app.security.SecurityController;
import app.security.SecurityMessages;
import app.security.TokenSecurity;
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

import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class SecurityRoutesTest {
    private static final String ISSUER = "security-test";
    private static final String SECRET_KEY = "test-only-secret-key-at-least-32-bytes";
    private static final PostgreSQLContainer database = new PostgreSQLContainer("postgres:16.2");
    private static Javalin app;
    private static EntityManagerFactory previousEmf;
    private static EntityManagerFactory testEmf;

    @BeforeAll
    static void setUp() {
        // Keep registration and login tests separate from the application database.
        previousEmf = HibernateConfig.emf;
        database.start();
        testEmf = new Configuration()
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Role.class)
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setProperty("hibernate.connection.url", database.getJdbcUrl())
                .setProperty("hibernate.connection.username", database.getUsername())
                .setProperty("hibernate.connection.password", database.getPassword())
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .buildSessionFactory();
        HibernateConfig.emf = testEmf;

        SecurityController controller = new SecurityController(new UserDAO(), ISSUER, 3600000, SECRET_KEY);
        SecurityRoutes routes = new SecurityRoutes(controller);
        app = Javalin.create(config -> {
            config.router.apiBuilder(routes.getSecurityRoutes());
            config.router.apiBuilder(routes.getSecuredRoutes());
        });
        ExceptionHandlers.register(app);
        app.beforeMatched(controller::authenticate);
        app.beforeMatched(controller::authorize);
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
    void registerLoginAndCheckUserRole() {
        UserDTO credentials = new UserDTO("security-user", "test-password");

        given()
                .contentType(ContentType.JSON)
                .body(credentials)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(HttpStatus.OK.getCode())
                .body("username", equalTo("security-user"))
                .body("token", not(emptyOrNullString()));

        String token = given()
                .contentType(ContentType.JSON)
                .body(credentials)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(HttpStatus.OK.getCode())
                .extract().path("token");

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/auth/verify")
                .then()
                .statusCode(HttpStatus.OK.getCode())
                .body("username", equalTo("security-user"))
                .body("roles", hasItem("USER"));

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/protected/user_demo")
                .then()
                .statusCode(HttpStatus.OK.getCode());

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/protected/admin_demo")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.getCode())
                .body("status", equalTo(HttpStatus.FORBIDDEN.getCode()));
    }

    @Test
    void unknownUserAndWrongPasswordReturnUnauthorized() {
        given()
                .contentType(ContentType.JSON)
                .body(new UserDTO("unknown-user", "test-password"))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.getCode())
                .body("message", equalTo(SecurityMessages.UNAUTHORIZED_LOGIN));

        given()
                .contentType(ContentType.JSON)
                .body(new UserDTO("password-user", "correct-password"))
                .when()
                .post("/auth/register")
                .then()
                .statusCode(HttpStatus.OK.getCode());

        given()
                .contentType(ContentType.JSON)
                .body(new UserDTO("password-user", "wrong-password"))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.getCode())
                .body("message", equalTo(SecurityMessages.UNAUTHORIZED_LOGIN));
    }

    @Test
    void malformedHeaderAndInvalidTokensReturnUnauthorized() throws Exception {
        given()
                .header("Authorization", "Bearer")
                .when()
                .get("/protected/user_demo")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.getCode())
                .body("message", equalTo(SecurityMessages.MALFORMED_HEADER));

        TokenSecurity tokens = new TokenSecurity();
        UserDTO user = new UserDTO("token-user", Set.of("USER"));
        String wrongSignature = tokens.createToken(user, ISSUER, 3600000,
                "another-test-secret-key-at-least-32-bytes");

        given()
                .header("Authorization", "Bearer " + wrongSignature)
                .when()
                .get("/protected/user_demo")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.getCode())
                .body("message", equalTo(SecurityMessages.INVALID_TOKEN));

        String expiredToken = tokens.createToken(user, ISSUER, -1000, SECRET_KEY);

        given()
                .header("Authorization", "Bearer " + expiredToken)
                .when()
                .get("/protected/user_demo")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.getCode())
                .body("message", equalTo(SecurityMessages.INVALID_TOKEN));

        String wrongIssuer = tokens.createToken(user, "another-issuer", 3600000, SECRET_KEY);

        given()
                .header("Authorization", "Bearer " + wrongIssuer)
                .when()
                .get("/protected/user_demo")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.getCode())
                .body("message", equalTo(SecurityMessages.INVALID_TOKEN));
    }
}
