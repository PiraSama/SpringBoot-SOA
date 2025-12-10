package com.example.service;

import com.example.dto.LoginRequest;
import com.example.dto.LoginResponse;
import com.example.dto.AuthResponse;
import com.example.entity.User;
import com.example.repository.UserRepository;
import com.example.config.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    /**
     * Đăng nhập người dùng
     */
    public LoginResponse login(LoginRequest loginRequest) {
        LoginResponse response = new LoginResponse();
        
        try {
            // Tìm user theo username
            Optional<User> userOptional = userRepository.findByUserName(loginRequest.getUserName());
            
            if (!userOptional.isPresent()) {
                response.setSuccess(false);
                response.setMessage("User not found");
                return response;
            }
            
            User user = userOptional.get();
            
            // Kiểm tra password (so sánh mã hoá)
            String encryptedPassword = hashPassword(loginRequest.getPassword());
            if (!user.getPassword().equals(encryptedPassword)) {
                response.setSuccess(false);
                response.setMessage("Invalid password");
                return response;
            }
            
            // Tạo JWT token
            String token = jwtTokenProvider.generateToken(user.getIdUser(), user.getUserName());
            
            // Lưu token vào database
            user.setToken(token);
            user.setUpdatedAt(System.currentTimeMillis());
            userRepository.save(user);
            
            // Trả về response
            response.setSuccess(true);
            response.setMessage("Login successful");
            response.setToken(token);
            response.setUserId(user.getIdUser());
            response.setUserName(user.getUserName());
            
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Login failed: " + e.getMessage());
            return response;
        }
    }
    
    /**
     * Xác thực token
     */
    public AuthResponse authenticateToken(String token) {
        AuthResponse response = new AuthResponse();
        
        try {
            // Xác thực token
            if (!jwtTokenProvider.validateToken(token)) {
                response.setValid(false);
                response.setMessage("Invalid token");
                return response;
            }
            
            // Lấy user ID từ token
            Integer userId = jwtTokenProvider.getUserIdFromToken(token);
            if (userId == null) {
                response.setValid(false);
                response.setMessage("Cannot extract user ID from token");
                return response;
            }
            
            // Tìm user trong database
            Optional<User> userOptional = userRepository.findById(userId);
            if (!userOptional.isPresent()) {
                response.setValid(false);
                response.setMessage("User not found");
                return response;
            }
            
            User user = userOptional.get();
            
            // Kiểm tra token trong database
            if (user.getToken() == null || !user.getToken().equals(token)) {
                response.setValid(false);
                response.setMessage("Token mismatch");
                return response;
            }
            
            // Token hợp lệ
            response.setValid(true);
            response.setMessage("Token is valid");
            response.setUserId(user.getIdUser());
            response.setUserName(user.getUserName());
            
            return response;
        } catch (Exception e) {
            response.setValid(false);
            response.setMessage("Authentication failed: " + e.getMessage());
            return response;
        }
    }
    
    /**
     * Mã hoá mật khẩu bằng MD5
     */
    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }
    
    /**
     * Tạo user mới (cho test)
     */
    public User createUser(String userName, String password, String email) {
        User user = new User();
        user.setUserName(userName);
        user.setPassword(hashPassword(password));
        user.setEmail(email);
        user.setCreatedAt(System.currentTimeMillis());
        user.setUpdatedAt(System.currentTimeMillis());
        return userRepository.save(user);
    }
    
    /**
     * Đăng xuất
     */
    public Boolean logout(Integer userId) {
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setToken(null);
                user.setUpdatedAt(System.currentTimeMillis());
                userRepository.save(user);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
