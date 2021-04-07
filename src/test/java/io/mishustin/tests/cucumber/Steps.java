package io.mishustin.tests.cucumber;

import io.cucumber.java.BeforeStep;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.mishustin.krypton.TestConfiguration;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.testng.Assert.*;

public class Steps {

    private static Response response;

    @Given("API Client is initialized")
    public void initAPIClient() {
        RestAssured.baseURI = TestConfiguration.jsonHost;
    }

    @Given("User is initialized")
    public void userIsInitialized() {
        System.out.println("Access granted");
    }

    @Given("Service is available")
    public void checkService() {
        assertEquals(RestAssured.get().getStatusCode(), 200);
    }

    @When("I execute {word} {word} request")
    public void executeRequest(String method, String endpoint) {
        response = RestAssured.get(endpoint);
    }

    @Then("I should receive {int} response code")
    public void shouldReceiveCode(int code) {
        response.then().assertThat().statusCode(code);
    }
}
