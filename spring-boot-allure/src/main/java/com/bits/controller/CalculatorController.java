package com.bits.controller;

import com.bits.ResponseTransfer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class CalculatorController {

    @RequestMapping("/")
    public ResponseTransfer get() {
        return new ResponseTransfer(200);
    }

    @RequestMapping("/calc/add")
    public ResponseTransfer add(int a, int b) {
        return new ResponseTransfer(a + b);
    }

    @RequestMapping("/calc/sub")
    public ResponseTransfer subtract(int a, int b) {
        return new ResponseTransfer( a - b);
    }

    @RequestMapping("/calc/mul")
    public ResponseTransfer multiply(int a, int b) {
        return new ResponseTransfer( a * b);
    }

    @RequestMapping("/calc/div")
    public ResponseTransfer divide(int a, int b) {
        return new ResponseTransfer( a / b);
    }
}
