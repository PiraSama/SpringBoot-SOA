package com.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Interceptor xác thực JWT token trước khi xử lý request
 */
@Component
public class JwtAuthInterceptor implements HandlerInterceptor {
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Kiểm tra token trước khi request được xử lý
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        System.out.println("[Interceptor] Path: " + path + ", Method: " + method);
        
        // Bypass các endpoint công khai (chỉ cho phép GET)
        if (isPublicEndpoint(path, method)) {
            System.out.println("[Interceptor] Public endpoint - ALLOW");
            return true;
        }
        
        System.out.println("[Interceptor] Protected endpoint - checking token");
        
        // Lấy Authorization header
        String authHeader = request.getHeader("Authorization");
        
        System.out.println("[Interceptor] Auth header: " + authHeader);
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("[Interceptor] No/invalid auth header - BLOCK");
            return sendError(response, "Missing or invalid Authorization header");
        }
        
        // Trích xuất token từ "Bearer <token>"
        String token = authHeader.substring(7);
        
        // Xác thực token
        if (!jwtTokenProvider.validateToken(token)) {
            System.out.println("[Interceptor] Invalid token - BLOCK");
            return sendError(response, "Invalid or expired token");
        }
        
        System.out.println("[Interceptor] Valid token - ALLOW");
        
        // Lưu thông tin user vào request attributes
        Integer userId = jwtTokenProvider.getUserIdFromToken(token);
        String userName = jwtTokenProvider.getUserNameFromToken(token);
        
        request.setAttribute("userId", userId);
        request.setAttribute("userName", userName);
        
        return true;
    }
    
    /**
     * Kiểm tra xem endpoint có phải công khai không
     */
    private boolean isPublicEndpoint(String path, String method) {
        // Chỉ cho phép GET /products và /products/{id}
        if ("GET".equalsIgnoreCase(method)) {
            return path.equals("/") || path.startsWith("/products") || path.contains("/h2-console");
        }
        return path.equals("/");
    }
    
    /**
     * Gửi lỗi xác thực
     */
    private boolean sendError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        
        String errorResponse = objectMapper.writeValueAsString(
            new ErrorResponse(false, message)
        );
        response.getWriter().write(errorResponse);
        return false;
    }
    
    /**
     * Inner class cho error response
     */
    static class ErrorResponse {
        public boolean success;
        public String message;
        
        ErrorResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
}
