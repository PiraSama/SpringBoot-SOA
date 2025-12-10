package com.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

@Component
public class JwtAuthInterceptor implements HandlerInterceptor {
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // Public endpoints: GET /orders, GET /orders/{id}, GET /order_items, etc (only GET)
        if (isPublicEndpoint(path, method)) {
            return true;
        }
        
        // Protected endpoints: require JWT token
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return sendError(response, "Missing or invalid Authorization header");
        }
        
        String token = authHeader.substring(7);
        
        if (!jwtTokenProvider.validateToken(token)) {
            return sendError(response, "Invalid or expired token");
        }
        
        Integer userId = jwtTokenProvider.getUserIdFromToken(token);
        String userName = jwtTokenProvider.getUserNameFromToken(token);
        
        request.setAttribute("userId", userId);
        request.setAttribute("userName", userName);
        
        return true;
    }
    
    private boolean isPublicEndpoint(String path, String method) {
        if ("GET".equalsIgnoreCase(method)) {
            return path.equals("/") || path.startsWith("/orders") || path.startsWith("/order_items") || path.contains("/h2-console");
        }
        return path.equals("/");
    }
    
    private boolean sendError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        
        String errorResponse = objectMapper.writeValueAsString(
            new ErrorResponse(false, message)
        );
        response.getWriter().write(errorResponse);
        return false;
    }
    
    static class ErrorResponse {
        public boolean success;
        public String message;
        
        ErrorResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
}
