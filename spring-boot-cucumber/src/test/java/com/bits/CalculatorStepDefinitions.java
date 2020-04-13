package com.bits;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;

public class CalculatorStepDefinitions extends CalculatorControllerIT {

    private ResponseEntity<String> response;
    String url = DEFAULT_URL + "calc/";

    @When("the client calls \\/calc\\/add with values {int} and {int}")
    public void the_client_calls_arithmetic_add_with_values_and(Integer int1, Integer int2) {
        response = restTemplate.getForEntity(url + "add?a=" + int1 + "&b="+ int2, String.class);
    }

    @When("the client calls \\/calc\\/sub with values {int} and {int}")
    public void the_client_calls_calc_sub_with_values_and(Integer int1, Integer int2) {
        response = restTemplate.getForEntity(url + "sub?a=" + int1 + "&b="+ int2, String.class);
    }

    @When("the client calls \\/calc\\/mul with values {int} and {int}")
    public void the_client_calls_calc_mul_with_values_and(Integer int1, Integer int2) {
        response = restTemplate.getForEntity(url + "mul?a=" + int1 + "&b="+ int2, String.class);
    }

    @When("the client calls \\/calc\\/div with values {int} and {int}")
    public void the_client_calls_calc_div_with_values_and(Integer int1, Integer int2) {
        response = restTemplate.getForEntity(url + "div?a=" + int1 + "&b="+ int2, String.class);
    }

    @Then("the client receives answer as {int}")
    public void the_client_receives_answer_as(Integer result) throws JsonProcessingException {
        assertEquals(result, getResponseValue(response));
    }

    private Integer getResponseValue(ResponseEntity<String> response) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());
        JsonNode answer = root.path("answer");
        return answer.asInt();
    }
}
