package app.routes;

import app.config.ApplicationConfig;
import app.dtos.accountdtos.AccountSearchDTO;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class AccountRoutesTest {

    public static ApplicationConfig applicationConfig = new ApplicationConfig();
    @BeforeAll
    static void setUp() {
        applicationConfig.appStart();


        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 7070;
    }

    @Test
    void getAccountRoutesTest() {
        given()
                .when()
                .get("/account/test")
                .then()
                .statusCode(200)
                .body("msg", equalTo("Hello from Open"));
    }

    @Test
    void getAccountRoutesFind() {
        AccountSearchDTO search = new AccountSearchDTO("cuc", "2808", "eun1");

        given()
                .contentType(ContentType.JSON)
                .body(search)
                .when()
                .post("/account/find")
                .then()
                .statusCode(200)
                .body("accountIdentity.gameName", equalTo("CUC"))
                .body("accountEntries.find { it.queueType == 'RANKED_SOLO_5x5' }.tier",
                        equalTo("EMERALD")); //Account might be higher rank, test to find out.

    }
}