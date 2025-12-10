package com.example.controller;

import com.example.dto.*;
import com.example.service.ReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reports/orders")
public class OrderReportController {
    
    @Autowired
    private ReportingService reportingService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderReportResponse>>> getAllOrderReports() {
        try {
            List<OrderReportResponse> reports = reportingService.getAllOrderReports();
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Order reports retrieved successfully", reports)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<List<OrderReportResponse>>(false, "Error retrieving reports: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderReportResponse>> getOrderReportById(@PathVariable Integer id) {
        try {
            Optional<OrderReportResponse> report = reportingService.getOrderReportById(id);
            
            if (report.isPresent()) {
                return ResponseEntity.ok(
                    new ApiResponse<>(true, "Order report retrieved successfully", report.get())
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<OrderReportResponse>(false, "Order report not found", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<OrderReportResponse>(false, "Error retrieving report: " + e.getMessage(), null));
        }
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<OrderReportResponse>> createOrderReport(
            @RequestParam Integer orderId,
            @RequestParam BigDecimal totalRevenue,
            @RequestParam BigDecimal totalCost,
            HttpServletRequest httpRequest) {
        try {
            OrderReportResponse report = reportingService.createOrderReport(orderId, totalRevenue, totalCost);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Order report created successfully", report));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<OrderReportResponse>(false, "Error creating report: " + e.getMessage(), null));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteOrderReport(
            @PathVariable Integer id,
            HttpServletRequest httpRequest) {
        try {
            if (reportingService.deleteOrderReport(id)) {
                return ResponseEntity.ok(
                    new ApiResponse<>(true, "Order report deleted successfully", "")
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<String>(false, "Order report not found", ""));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<String>(false, "Error deleting report: " + e.getMessage(), ""));
        }
    }
}
