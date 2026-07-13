package br.edu.ifg;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;

@QuarkusTest
class AuthControllerTest {

    @Test
    void loginShouldAcceptJsonBodyAndReturnToken() {
        given()
            .contentType("application/json")
            .body(Map.of("email", "admin@housitalian.com", "senha", "admin123"))
        .when()
            .post("/api/auth/login")
        .then()
            .statusCode(200);
    }
}
