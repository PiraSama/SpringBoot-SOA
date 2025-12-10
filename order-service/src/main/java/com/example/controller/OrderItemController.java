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
@RequestMapping("/order_items")
public class OrderItemController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * GET /order_items - Lấy danh sách tất cả mặt hàng (Public)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderItemResponse>>> getAllOrderItems() {
        try {
            List<OrderItemResponse> items = orderService.getAllOrderItems();
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Order items retrieved successfully", items)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<List<OrderItemResponse>>(false, "Error retrieving order items: " + e.getMessage(), null));
        }
    }
    
    /**
     * GET /order_items/{id} - Lấy thông tin chi tiết một mặt hàng (Public)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderItemResponse>> getOrderItemById(@PathVariable Integer id) {
        try {
            Optional<OrderItemResponse> item = orderService.getOrderItemById(id);
            
            if (item.isPresent()) {
                return ResponseEntity.ok(
                    new ApiResponse<>(true, "Order item retrieved successfully", item.get())
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<OrderItemResponse>(false, "Order item not found", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<OrderItemResponse>(false, "Error retrieving order item: " + e.getMessage(), null));
        }
    }
    
    /**
     * GET /order_items/order/{orderId} - Lấy danh sách mặt hàng theo Order ID (Public)
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<List<OrderItemResponse>>> getOrderItemsByOrderId(@PathVariable Integer orderId) {
        try {
            List<OrderItemResponse> items = orderService.getOrderItemsByOrderId(orderId);
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Order items retrieved successfully", items)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<List<OrderItemResponse>>(false, "Error retrieving order items: " + e.getMessage(), null));
        }
    }
    
    /**
     * POST /order_items/{orderId} - Thêm mặt hàng vào đơn hàng (Protected)
     */
    @PostMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderItemResponse>> addOrderItem(
            @PathVariable Integer orderId,
            @RequestBody CreateOrderItemRequest request,
            HttpServletRequest httpRequest) {
        try {
            if (request.getProductId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<OrderItemResponse>(false, "Product ID is required", null));
            }
            
            if (request.getProductName() == null || request.getProductName().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<OrderItemResponse>(false, "Product name is required", null));
            }
            
            if (request.getQuantity() == null || request.getQuantity() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<OrderItemResponse>(false, "Quantity must be greater than 0", null));
            }
            
            if (request.getUnitPrice() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<OrderItemResponse>(false, "Unit price is required", null));
            }
            
            OrderItemResponse item = orderService.addOrderItem(orderId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Order item created successfully", item));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<OrderItemResponse>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<OrderItemResponse>(false, "Error creating order item: " + e.getMessage(), null));
        }
    }
    
    /**
     * PUT /order_items/{id} - Cập nhật mặt hàng (Protected)
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderItemResponse>> updateOrderItem(
            @PathVariable Integer id,
            @RequestBody CreateOrderItemRequest request,
            HttpServletRequest httpRequest) {
        try {
            if (request.getQuantity() == null || request.getQuantity() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<OrderItemResponse>(false, "Quantity must be greater than 0", null));
            }
            
            Optional<OrderItemResponse> item = orderService.updateOrderItem(id, request);
            
            if (item.isPresent()) {
                return ResponseEntity.ok(
                    new ApiResponse<>(true, "Order item updated successfully", item.get())
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<OrderItemResponse>(false, "Order item not found", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<OrderItemResponse>(false, "Error updating order item: " + e.getMessage(), null));
        }
    }
    
    /**
     * DELETE /order_items/{id} - Xóa một mặt hàng (Protected)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteOrderItem(
            @PathVariable Integer id,
            HttpServletRequest httpRequest) {
        try {
            if (orderService.deleteOrderItem(id)) {
                return ResponseEntity.ok(
                    new ApiResponse<>(true, "Order item deleted successfully", "")
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Order item not found", ""));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Error deleting order item: " + e.getMessage(), ""));
        }
    }
}
