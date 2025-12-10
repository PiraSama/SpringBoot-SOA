package com.example.service;

import com.example.entity.OrderReport;
import com.example.entity.ProductReport;
import com.example.dto.OrderReportResponse;
import com.example.dto.ProductReportResponse;
import com.example.repository.OrderReportRepository;
import com.example.repository.ProductReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReportingService {
    
    @Autowired
    private OrderReportRepository orderReportRepository;
    
    @Autowired
    private ProductReportRepository productReportRepository;
    
    // Order Report CRUD
    public List<OrderReportResponse> getAllOrderReports() {
        return orderReportRepository.findAll()
            .stream()
            .map(this::convertToOrderReportResponse)
            .collect(Collectors.toList());
    }
    
    public Optional<OrderReportResponse> getOrderReportById(Integer id) {
        return orderReportRepository.findById(id)
            .map(this::convertToOrderReportResponse);
    }
    
    public OrderReportResponse createOrderReport(Integer orderId, BigDecimal totalRevenue, BigDecimal totalCost) {
        OrderReport report = new OrderReport();
        report.setOrderId(orderId);
        report.setTotalRevenue(totalRevenue);
        report.setTotalCost(totalCost);
        report.setTotalProfit(totalRevenue.subtract(totalCost));
        
        OrderReport saved = orderReportRepository.save(report);
        return convertToOrderReportResponse(saved);
    }
    
    public boolean deleteOrderReport(Integer id) {
        if (orderReportRepository.existsById(id)) {
            orderReportRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // Product Report CRUD
    public List<ProductReportResponse> getAllProductReports() {
        return productReportRepository.findAll()
            .stream()
            .map(this::convertToProductReportResponse)
            .collect(Collectors.toList());
    }
    
    public Optional<ProductReportResponse> getProductReportById(Integer id) {
        return productReportRepository.findById(id)
            .map(this::convertToProductReportResponse);
    }
    
    public ProductReportResponse createProductReport(
            Integer orderReportId,
            Integer productId,
            Integer totalSold,
            BigDecimal revenue,
            BigDecimal cost) {
        
        ProductReport report = new ProductReport();
        if (orderReportId != null) {
            OrderReport orderReport = orderReportRepository.findById(orderReportId)
                .orElse(null);
            report.setOrderReport(orderReport);
        }
        report.setProductId(productId);
        report.setTotalSold(totalSold);
        report.setRevenue(revenue);
        report.setCost(cost);
        report.setProfit(revenue.subtract(cost));
        
        ProductReport saved = productReportRepository.save(report);
        return convertToProductReportResponse(saved);
    }
    
    public boolean deleteProductReport(Integer id) {
        if (productReportRepository.existsById(id)) {
            productReportRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    private OrderReportResponse convertToOrderReportResponse(OrderReport report) {
        return new OrderReportResponse(
            report.getId(),
            report.getOrderId(),
            report.getTotalRevenue(),
            report.getTotalCost(),
            report.getTotalProfit()
        );
    }
    
    private ProductReportResponse convertToProductReportResponse(ProductReport report) {
        return new ProductReportResponse(
            report.getId(),
            report.getProductId(),
            report.getTotalSold(),
            report.getRevenue(),
            report.getCost(),
            report.getProfit()
        );
    }
}
