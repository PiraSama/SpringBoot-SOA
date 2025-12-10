package com.example.config;

import com.example.entity.Product;
import com.example.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * DataInitializer - Khởi tạo dữ liệu test khi ứng dụng khởi động
 */
@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("========== Initializing Product Test Data ==========");
        
        // Khởi tạo 5 sản phẩm test
        createProduct("Laptop HP Pavilion", "Laptop văn phòng cao cấp", 
                     new BigDecimal("12500000"), 15);
        
        createProduct("Samsung Galaxy S24", "Điện thoại cao cấp 2024", 
                     new BigDecimal("22990000"), 25);
        
        createProduct("AirPods Pro", "Tai nghe không dây chính hãng", 
                     new BigDecimal("4890000"), 40);
        
        createProduct("iPad Air", "Máy tính bảng Apple", 
                     new BigDecimal("15490000"), 18);
        
        createProduct("Sony WH-1000XM5", "Tai nghe chống ồn", 
                     new BigDecimal("7990000"), 12);
        
        long totalProducts = productRepository.count();
        System.out.println("✓ Total products created: " + totalProducts);
        System.out.println("===================================================");
    }
    
    private void createProduct(String name, String description, BigDecimal price, Integer quantity) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setQuantity(quantity);
        
        productRepository.save(product);
        System.out.println("✓ Created product: " + name);
    }
}
