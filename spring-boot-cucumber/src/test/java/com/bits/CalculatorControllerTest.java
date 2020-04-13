package com.bits;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.jupiter.api.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources")
public class CalculatorControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void testClientCallsArithmeticAddWithValues() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/calc/add/")
                .accept(MediaType.APPLICATION_JSON)
                .param("a","1")
                .param("b","2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value(3));
    }

    @Test
    public void testClientCallsArithmeticSubWithValues() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/calc/sub/")
                .accept(MediaType.APPLICATION_JSON)
                .param("a","6")
                .param("b","3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value(3));
    }

    @Test
    public void testClientCallsArithmeticDivWithValues() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/calc/div/")
                .accept(MediaType.APPLICATION_JSON)
                .param("a","6")
                .param("b","2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value(3));
    }

    @Test
    public void testClientCallsArithmeticMulWithValues() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/calc/mul/")
                .accept(MediaType.APPLICATION_JSON)
                .param("a","1")
                .param("b","3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value(3));
    }

}
