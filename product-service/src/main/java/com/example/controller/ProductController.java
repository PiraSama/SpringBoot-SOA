package com.example.controller;

import com.example.dto.ApiResponse;
import com.example.dto.CreateProductRequest;
import com.example.dto.ProductResponse;
import com.example.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Optional;

/**
 * Controller xử lý các request liên quan đến sản phẩm
 */
@RestController
@RequestMapping("/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    /**
     * GET /products - Lấy danh sách tất cả sản phẩm (Public endpoint)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts(
            @RequestParam(required = false) String search) {
        
        try {
            List<ProductResponse> products;
            
            if (search != null && !search.trim().isEmpty()) {
                products = productService.searchProductsByName(search);
            } else {
                products = productService.getAllProducts();
            }
            
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Products retrieved successfully", products)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Error retrieving products: " + e.getMessage()));
        }
    }
    
    /**
     * GET /products/{id} - Lấy thông tin chi tiết một sản phẩm (Public endpoint)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Integer id) {
        try {
            Optional<ProductResponse> product = productService.getProductById(id);
            
            if (product.isPresent()) {
                return ResponseEntity.ok(
                    new ApiResponse<>(true, "Product retrieved successfully", product.get())
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Product not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Error retrieving product: " + e.getMessage()));
        }
    }
    
    /**
     * POST /products - Thêm sản phẩm mới (Protected - Cần xác thực JWT)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @RequestBody CreateProductRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            // Kiểm tra authorization (được kiểm tra bởi interceptor)
            Integer userId = (Integer) httpRequest.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Unauthorized"));
            }
            
            // Validate input
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Product name is required"));
            }
            if (request.getPrice() == null || request.getPrice().signum() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Product price must be greater than 0"));
            }
            if (request.getQuantity() == null || request.getQuantity() < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Product quantity cannot be negative"));
            }
            
            ProductResponse product = productService.createProduct(request);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Product created successfully", product));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Error creating product: " + e.getMessage()));
        }
    }
    
    /**
     * PUT /products/{id} - Cập nhật sản phẩm (Protected - Cần xác thực JWT)
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Integer id,
            @RequestBody CreateProductRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            // Kiểm tra authorization
            Integer userId = (Integer) httpRequest.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Unauthorized"));
            }
            
            // Validate input (nếu được cung cấp)
            if (request.getName() != null && request.getName().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Product name cannot be empty"));
            }
            if (request.getPrice() != null && request.getPrice().signum() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Product price must be greater than 0"));
            }
            if (request.getQuantity() != null && request.getQuantity() < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Product quantity cannot be negative"));
            }
            
            Optional<ProductResponse> updatedProduct = productService.updateProduct(id, request);
            
            if (updatedProduct.isPresent()) {
                return ResponseEntity.ok(
                    new ApiResponse<>(true, "Product updated successfully", updatedProduct.get())
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Product not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Error updating product: " + e.getMessage()));
        }
    }
    
    /**
     * DELETE /products/{id} - Xóa sản phẩm (Protected - Cần xác thực JWT)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable Integer id,
            HttpServletRequest httpRequest) {
        
        try {
            // Kiểm tra authorization
            Integer userId = (Integer) httpRequest.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Unauthorized"));
            }
            
            if (productService.deleteProduct(id)) {
                return ResponseEntity.ok(
                    new ApiResponse<>(true, "Product deleted successfully")
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Product not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Error deleting product: " + e.getMessage()));
        }
    }
    
    /**
     * GET / - Root endpoint trả về API info (Public)
     */
    @GetMapping("/")
    public ResponseEntity<ApiResponse<?>> getInfo() {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Product Management Service v1.0")
        );
    }
}
