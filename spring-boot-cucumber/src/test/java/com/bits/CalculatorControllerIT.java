package com.bits;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(
        classes = Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public abstract class CalculatorControllerIT {

    protected TestRestTemplate restTemplate = new TestRestTemplate();

    protected final String DEFAULT_URL = "http://localhost:55002/";

}
