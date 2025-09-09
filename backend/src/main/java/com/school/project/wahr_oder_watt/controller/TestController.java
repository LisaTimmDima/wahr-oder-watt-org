package com.school.project.wahr_oder_watt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String testRoot() {
        return "Hello from Spring Boot!";
    }
}
