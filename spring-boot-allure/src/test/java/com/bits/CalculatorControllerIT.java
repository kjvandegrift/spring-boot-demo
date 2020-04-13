package com.bits;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.Issue;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.qameta.allure.Attachment;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("This is the Epic Annotation on the test class")
@Feature("This is the Feature annotation on the test class")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CalculatorControllerIT {

    @LocalServerPort
    private int port;

    private String url;

    private ResponseEntity<String> response;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    public void setUp() throws Exception {
        url = (new URL("http://localhost:" + port + "/calc/")).toString();
    }

    @Test
    @Story("This is the story annotation on the test method")
    @Description("This test method calls helper methods annotated with @Step")
    public void testClientCallsArithmeticAddWithValues() throws JsonProcessingException {
        response = restTemplate.getForEntity(url + "add?a=" + getFirstAddValue("1") + "&b=" + getSecondAddValue("2"), String.class);
        assertEquals(3, getResponseValue(response));

    }

    @Test
    @Issue("This is the Issue annotation")
    public void testClientCallsArithmeticSubWithValues() throws JsonProcessingException {
        response = restTemplate.getForEntity(url + "sub?a=5&b=3", String.class);
        assertEquals(2, getResponseValue(response));
    }

    @Test
    @Description("This is test method calls a method that inserts an attachment into the report")
    public void testClientCallsArithmeticDivWithValues() throws IOException {
        response = restTemplate.getForEntity(url + "div?a=6&b=2", String.class);
        stepWithAttachment();
        assertEquals(3, getResponseValue(response));
    }

    @Test
    @Description("This test has a CRITICAL severity. Possible values include BLOCKER, CRITICAL, NORMAL, MINOR, TRIVIAL")
    @Severity(SeverityLevel.CRITICAL)
    public void testClientCallsArithmeticMulWithValues() throws JsonProcessingException {
        response = restTemplate.getForEntity(url + "mul?a=2&b=3", String.class);
        assertEquals(6, getResponseValue(response));
    }

    @Test
    @Description("This test is supposed to fail")
    @Severity(SeverityLevel.CRITICAL)
    public void tesFailure() {
        assertEquals(1, 2);
    }


    @Step("Get First add Value {0}")
    private String getFirstAddValue(String num) {
        return num;
    }

    @Step("Get Second add value {0}")
    private String getSecondAddValue(String num) {
        return num;
    }

    @Step("Attachment to be placed in the test report")
    @Attachment
    private byte[] stepWithAttachment() throws IOException {
        return Files.readAllBytes(
                new File(getClass().getClassLoader().getResource("images/kitten.jpg").getFile()).toPath()
        );
    }

    private Integer getResponseValue(ResponseEntity<String> response) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());
        JsonNode answer = root.path("answer");
        return answer.asInt();
    }

}
