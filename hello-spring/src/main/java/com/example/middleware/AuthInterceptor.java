package com.example.middleware;

import com.example.config.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Cho phép các request không cần xác thực
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // Bypass cho root path và public endpoints
        if (path.equals("/") || path.equals("") || path.contains("/h2-console")) {
            return true;
        }
        
        // Bypass cho login, auth endpoints
        if ((path.contains("/api/login") || path.contains("/api/auth") || 
             path.contains("/swagger") || path.contains("/api-docs")) && 
            !method.equals("GET")) {
            return true;
        }
        
        // Kiểm tra authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || authHeader.isEmpty()) {
            // Không require auth cho GET hello
            if (path.contains("/api/hello")) {
                return true;
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Authorization header is missing\"}");
            return false;
        }
        
        // Lấy token
        String token = null;
        if (authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else {
            token = authHeader;
        }
        
        // Xác thực token
        if (!jwtTokenProvider.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid or expired token\"}");
            return false;
        }
        
        return true;
    }
}
