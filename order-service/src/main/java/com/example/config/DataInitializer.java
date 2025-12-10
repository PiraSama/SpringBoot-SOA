package com.example.config;

import com.example.entity.Order;
import com.example.entity.OrderItem;
import com.example.repository.OrderRepository;
import com.example.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("========== Initializing Order Test Data ==========");
        
        // Create sample orders
        Order order1 = new Order();
        order1.setCustomerName("Nguyễn Văn A");
        order1.setCustomerEmail("customer1@example.com");
        order1.setStatus("completed");
        order1 = orderRepository.save(order1);
        System.out.println("✓ Created order: " + order1.getCustomerName());
        
        Order order2 = new Order();
        order2.setCustomerName("Trần Thị B");
        order2.setCustomerEmail("customer2@example.com");
        order2.setStatus("pending");
        order2 = orderRepository.save(order2);
        System.out.println("✓ Created order: " + order2.getCustomerName());
        
        // Add items to order1
        OrderItem item1 = new OrderItem();
        item1.setOrder(order1);
        item1.setProductId(1);
        item1.setProductName("Laptop HP Pavilion");
        item1.setQuantity(1);
        item1.setUnitPrice(new BigDecimal("12500000"));
        item1.setTotalPrice(new BigDecimal("12500000"));
        orderItemRepository.save(item1);
        System.out.println("✓ Added item: " + item1.getProductName());
        
        OrderItem item2 = new OrderItem();
        item2.setOrder(order1);
        item2.setProductId(2);
        item2.setProductName("Samsung Galaxy S24");
        item2.setQuantity(2);
        item2.setUnitPrice(new BigDecimal("22990000"));
        item2.setTotalPrice(new BigDecimal("45980000"));
        orderItemRepository.save(item2);
        System.out.println("✓ Added item: " + item2.getProductName());
        
        // Add items to order2
        OrderItem item3 = new OrderItem();
        item3.setOrder(order2);
        item3.setProductId(3);
        item3.setProductName("AirPods Pro");
        item3.setQuantity(1);
        item3.setUnitPrice(new BigDecimal("4890000"));
        item3.setTotalPrice(new BigDecimal("4890000"));
        orderItemRepository.save(item3);
        System.out.println("✓ Added item: " + item3.getProductName());
        
        // Update order totals
        order1.setTotalAmount(new BigDecimal("58480000"));
        orderRepository.save(order1);
        
        order2.setTotalAmount(new BigDecimal("4890000"));
        orderRepository.save(order2);
        
        System.out.println("✓ Total orders created: 2");
        System.out.println("✓ Total items created: 3");
        System.out.println("===================================================");
    }
}
