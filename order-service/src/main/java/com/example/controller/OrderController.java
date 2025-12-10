package com.example.controller;

import com.example.dto.*;
import com.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    // Order endpoints
    
    /**
     * GET /orders - Lấy danh sách tất cả đơn hàng (Public)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        try {
            List<OrderResponse> orders = orderService.getAllOrders();
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Orders retrieved successfully", orders)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<List<OrderResponse>>(false, "Error retrieving orders: " + e.getMessage(), null));
        }
    }
    
    /**
     * GET /orders/{id} - Lấy thông tin chi tiết một đơn hàng (Public)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Integer id) {
        try {
            Optional<OrderResponse> order = orderService.getOrderById(id);
            
            if (order.isPresent()) {
                return ResponseEntity.ok(
                    new ApiResponse<>(true, "Order retrieved successfully", order.get())
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<OrderResponse>(false, "Order not found", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<OrderResponse>(false, "Error retrieving order: " + e.getMessage(), null));
        }
    }
    
    /**
     * POST /orders - Tạo đơn hàng mới (Protected)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @RequestBody CreateOrderRequest request,
            HttpServletRequest httpRequest) {
        try {
            if (request.getCustomerName() == null || request.getCustomerName().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<OrderResponse>(false, "Customer name is required", null));
            }
            
            if (request.getCustomerEmail() == null || request.getCustomerEmail().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<OrderResponse>(false, "Customer email is required", null));
            }
            
            OrderResponse order = orderService.createOrder(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Order created successfully", order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<OrderResponse>(false, "Error creating order: " + e.getMessage(), null));
        }
    }
    
    /**
     * PUT /orders/{id} - Cập nhật trạng thái đơn hàng (Protected)
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Integer id,
            @RequestParam String status,
            HttpServletRequest httpRequest) {
        try {
            if (status == null || status.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<OrderResponse>(false, "Status is required", null));
            }
            
            Optional<OrderResponse> order = orderService.updateOrderStatus(id, status);
            
            if (order.isPresent()) {
                return ResponseEntity.ok(
                    new ApiResponse<>(true, "Order updated successfully", order.get())
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<OrderResponse>(false, "Order not found", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<OrderResponse>(false, "Error updating order: " + e.getMessage(), null));
        }
    }
    
    /**
     * DELETE /orders/{id} - Xóa một đơn hàng (Protected)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteOrder(
            @PathVariable Integer id,
            HttpServletRequest httpRequest) {
        try {
            if (orderService.deleteOrder(id)) {
                return ResponseEntity.ok(
                    new ApiResponse<>(true, "Order deleted successfully", "")
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Order not found", ""));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Error deleting order: " + e.getMessage(), ""));
        }
    }
}
