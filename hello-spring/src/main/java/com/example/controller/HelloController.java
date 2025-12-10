package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HelloController {
    
    @GetMapping("/")
    public Map<String, Object> root() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello World from Spring Boot!");
        response.put("status", "API is running");
        response.put("version", "2.0");
        response.put("endpoints", new String[]{
            "GET /api/hello - Hello World",
            "POST /api/login - Login with credentials",
            "POST /api/auth - Verify JWT token",
            "POST /api/logout - Logout",
            "GET /h2-console - Database console"
        });
        return response;
    }
    
    @GetMapping("/api/hello")
    public String helloWorld() {
        return "Hello World from Spring Boot!";
    }
}
