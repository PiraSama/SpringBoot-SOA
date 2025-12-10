package com.example.config;

import com.example.entity.OrderReport;
import com.example.entity.ProductReport;
import com.example.repository.OrderReportRepository;
import com.example.repository.ProductReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private OrderReportRepository orderReportRepository;
    
    @Autowired
    private ProductReportRepository productReportRepository;
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("========== Initializing Reporting Test Data ==========");
        
        // Create sample order reports
        OrderReport report1 = new OrderReport();
        report1.setOrderId(1);
        report1.setTotalRevenue(new BigDecimal("58480000"));
        report1.setTotalCost(new BigDecimal("40000000"));
        report1.setTotalProfit(new BigDecimal("18480000"));
        report1 = orderReportRepository.save(report1);
        System.out.println("✓ Created order report: Order #1");
        
        OrderReport report2 = new OrderReport();
        report2.setOrderId(2);
        report2.setTotalRevenue(new BigDecimal("4890000"));
        report2.setTotalCost(new BigDecimal("3000000"));
        report2.setTotalProfit(new BigDecimal("1890000"));
        report2 = orderReportRepository.save(report2);
        System.out.println("✓ Created order report: Order #2");
        
        // Create sample product reports
        ProductReport product1 = new ProductReport();
        product1.setOrderReport(report1);
        product1.setProductId(1);
        product1.setTotalSold(1);
        product1.setRevenue(new BigDecimal("12500000"));
        product1.setCost(new BigDecimal("8000000"));
        product1.setProfit(new BigDecimal("4500000"));
        productReportRepository.save(product1);
        System.out.println("✓ Created product report: Product #1");
        
        ProductReport product2 = new ProductReport();
        product2.setOrderReport(report1);
        product2.setProductId(2);
        product2.setTotalSold(2);
        product2.setRevenue(new BigDecimal("45980000"));
        product2.setCost(new BigDecimal("32000000"));
        product2.setProfit(new BigDecimal("13980000"));
        productReportRepository.save(product2);
        System.out.println("✓ Created product report: Product #2");
        
        ProductReport product3 = new ProductReport();
        product3.setOrderReport(report2);
        product3.setProductId(3);
        product3.setTotalSold(1);
        product3.setRevenue(new BigDecimal("4890000"));
        product3.setCost(new BigDecimal("3000000"));
        product3.setProfit(new BigDecimal("1890000"));
        productReportRepository.save(product3);
        System.out.println("✓ Created product report: Product #3");
        
        System.out.println("✓ Total order reports: 2");
        System.out.println("✓ Total product reports: 3");
        System.out.println("===================================================");
    }
}
