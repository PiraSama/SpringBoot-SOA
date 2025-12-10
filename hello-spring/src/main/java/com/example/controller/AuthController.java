package com.example.controller;

import com.example.dto.LoginRequest;
import com.example.dto.LoginResponse;
import com.example.dto.AuthResponse;
import com.example.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    /**
     * API đăng nhập
     * POST /api/login
     * Body: { "userName": "user", "password": "password" }
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    /**
     * API xác thực token
     * POST /api/auth
     * Header: Authorization: Bearer <token>
     */
    @PostMapping("/auth")
    public ResponseEntity<AuthResponse> authenticate(@RequestHeader(name = "Authorization", required = false) String authHeader) {
        AuthResponse response = new AuthResponse();
        
        if (authHeader == null || authHeader.isEmpty()) {
            response.setValid(false);
            response.setMessage("Authorization header is missing");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        // Lấy token từ header (format: "Bearer <token>")
        String token = null;
        if (authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else {
            token = authHeader;
        }
        
        response = authService.authenticateToken(token);
        
        if (response.getValid()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    /**
     * API đăng xuất
     * POST /api/logout
     * Header: Authorization: Bearer <token>
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestHeader(name = "Authorization", required = false) String authHeader) {
        Map<String, Object> response = new HashMap<>();
        
        if (authHeader == null || authHeader.isEmpty()) {
            response.put("success", false);
            response.put("message", "Authorization header is missing");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        String token = null;
        if (authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else {
            token = authHeader;
        }
        
        AuthResponse authResponse = authService.authenticateToken(token);
        if (!authResponse.getValid()) {
            response.put("success", false);
            response.put("message", "Invalid token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        Boolean logoutResult = authService.logout(authResponse.getUserId());
        response.put("success", logoutResult);
        response.put("message", logoutResult ? "Logout successful" : "Logout failed");
        
        return ResponseEntity.ok(response);
    }
}
