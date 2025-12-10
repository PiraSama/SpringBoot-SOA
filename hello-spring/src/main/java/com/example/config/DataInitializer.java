package com.example.config;

import com.example.entity.User;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("========== Initializing Test Data ==========");
        
        // Kiểm tra xem đã có user chưa
        if (userRepository.findByUserName("admin").isEmpty()) {
            createTestUser("admin", "admin123", "admin@example.com");
            System.out.println("✓ Created user: admin");
        }
        
        if (userRepository.findByUserName("user").isEmpty()) {
            createTestUser("user", "user123", "user@example.com");
            System.out.println("✓ Created user: user");
        }
        
        if (userRepository.findByUserName("test").isEmpty()) {
            createTestUser("test", "test123", "test@example.com");
            System.out.println("✓ Created user: test");
        }
        
        long totalUsers = userRepository.count();
        System.out.println("Total users in database: " + totalUsers);
        System.out.println("=========================================");
    }
    
    private void createTestUser(String userName, String password, String email) throws Exception {
        User user = new User();
        user.setUserName(userName);
        user.setPassword(hashPassword(password));
        user.setEmail(email);
        user.setToken(null);
        user.setCreatedAt(System.currentTimeMillis());
        user.setUpdatedAt(System.currentTimeMillis());
        
        userRepository.save(user);
    }
    
    private String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : messageDigest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
