package io.mishustin.tests.api;

import io.mishustin.krypton.TestConfiguration;
import io.mishustin.krypton.listeners.TestNgListener;
import io.mishustin.krypton.annotation.Xfail;
import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(TestNgListener.class)
public class RestAssuredTest {

    @BeforeClass
    public void init() {
        RestAssured.baseURI = TestConfiguration.host;
    }

    @Test
    public void getFindByStatusShouldReturnStatus() {
        RestAssured
                .given().queryParam("status", "pending")
                .get("/pet/findByStatus")
                .then().statusCode(200);
    }

    @Test
    public void getPetShouldReturnPet() {
        RestAssured
                .get("/pet/1")
                .then().statusCode(200);
    }

    @Test
    public void getOrderShouldReturnOrder() {
        RestAssured
                .get("/order/7")
                .then().statusCode(200);
    }

    @Xfail
    @Test
    public void getUserShouldReturnUser() {
        RestAssured
                .get("/user/user1")
                .then().statusCode(200);
    }
}