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
@RequestMapping("/reports/products")
public class ProductReportController {
    
    @Autowired
    private ReportingService reportingService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductReportResponse>>> getAllProductReports() {
        try {
            List<ProductReportResponse> reports = reportingService.getAllProductReports();
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Product reports retrieved successfully", reports)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<List<ProductReportResponse>>(false, "Error retrieving reports: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductReportResponse>> getProductReportById(@PathVariable Integer id) {
        try {
            Optional<ProductReportResponse> report = reportingService.getProductReportById(id);
            
            if (report.isPresent()) {
                return ResponseEntity.ok(
                    new ApiResponse<>(true, "Product report retrieved successfully", report.get())
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<ProductReportResponse>(false, "Product report not found", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<ProductReportResponse>(false, "Error retrieving report: " + e.getMessage(), null));
        }
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<ProductReportResponse>> createProductReport(
            @RequestParam(required = false) Integer orderReportId,
            @RequestParam Integer productId,
            @RequestParam Integer totalSold,
            @RequestParam BigDecimal revenue,
            @RequestParam BigDecimal cost,
            HttpServletRequest httpRequest) {
        try {
            ProductReportResponse report = reportingService.createProductReport(
                orderReportId, productId, totalSold, revenue, cost
            );
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Product report created successfully", report));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<ProductReportResponse>(false, "Error creating report: " + e.getMessage(), null));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProductReport(
            @PathVariable Integer id,
            HttpServletRequest httpRequest) {
        try {
            if (reportingService.deleteProductReport(id)) {
                return ResponseEntity.ok(
                    new ApiResponse<>(true, "Product report deleted successfully", "")
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<String>(false, "Product report not found", ""));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<String>(false, "Error deleting report: " + e.getMessage(), ""));
        }
    }
}
