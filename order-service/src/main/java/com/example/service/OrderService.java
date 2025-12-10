package com.example.service;

import com.example.entity.Order;
import com.example.entity.OrderItem;
import com.example.dto.OrderResponse;
import com.example.dto.OrderItemResponse;
import com.example.dto.CreateOrderRequest;
import com.example.dto.CreateOrderItemRequest;
import com.example.repository.OrderRepository;
import com.example.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    // Order CRUD Operations
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
            .stream()
            .map(this::convertToOrderResponse)
            .collect(Collectors.toList());
    }
    
    public Optional<OrderResponse> getOrderById(Integer id) {
        return orderRepository.findById(id)
            .map(this::convertToOrderResponse);
    }
    
    public OrderResponse createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setStatus("pending");
        order.setTotalAmount(BigDecimal.ZERO);
        
        Order saved = orderRepository.save(order);
        return convertToOrderResponse(saved);
    }
    
    public Optional<OrderResponse> updateOrderStatus(Integer id, String status) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus(status);
            Order updated = orderRepository.save(order);
            return Optional.of(convertToOrderResponse(updated));
        }
        
        return Optional.empty();
    }
    
    public boolean deleteOrder(Integer id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // OrderItem CRUD Operations
    public List<OrderItemResponse> getAllOrderItems() {
        return orderItemRepository.findAll()
            .stream()
            .map(this::convertToOrderItemResponse)
            .collect(Collectors.toList());
    }
    
    public Optional<OrderItemResponse> getOrderItemById(Integer id) {
        return orderItemRepository.findById(id)
            .map(this::convertToOrderItemResponse);
    }
    
    public List<OrderItemResponse> getOrderItemsByOrderId(Integer orderId) {
        return orderItemRepository.findByOrderId(orderId)
            .stream()
            .map(this::convertToOrderItemResponse)
            .collect(Collectors.toList());
    }
    
    public OrderItemResponse addOrderItem(Integer orderId, CreateOrderItemRequest request) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProductId(request.getProductId());
        item.setProductName(request.getProductName());
        item.setQuantity(request.getQuantity());
        item.setUnitPrice(request.getUnitPrice());
        item.setTotalPrice(request.getUnitPrice().multiply(new BigDecimal(request.getQuantity())));
        
        OrderItem saved = orderItemRepository.save(item);
        
        // Recalculate order total
        recalculateOrderTotal(orderId);
        
        return convertToOrderItemResponse(saved);
    }
    
    public Optional<OrderItemResponse> updateOrderItem(Integer id, CreateOrderItemRequest request) {
        Optional<OrderItem> itemOpt = orderItemRepository.findById(id);
        
        if (itemOpt.isPresent()) {
            OrderItem item = itemOpt.get();
            item.setProductName(request.getProductName());
            item.setQuantity(request.getQuantity());
            item.setUnitPrice(request.getUnitPrice());
            item.setTotalPrice(request.getUnitPrice().multiply(new BigDecimal(request.getQuantity())));
            
            OrderItem updated = orderItemRepository.save(item);
            
            if (item.getOrder() != null) {
                recalculateOrderTotal(item.getOrder().getId());
            }
            
            return Optional.of(convertToOrderItemResponse(updated));
        }
        
        return Optional.empty();
    }
    
    public boolean deleteOrderItem(Integer id) {
        Optional<OrderItem> itemOpt = orderItemRepository.findById(id);
        
        if (itemOpt.isPresent()) {
            OrderItem item = itemOpt.get();
            Integer orderId = item.getOrder().getId();
            
            orderItemRepository.deleteById(id);
            recalculateOrderTotal(orderId);
            
            return true;
        }
        
        return false;
    }
    
    private void recalculateOrderTotal(Integer orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        BigDecimal total = orderItemRepository.findByOrderId(orderId)
            .stream()
            .map(OrderItem::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        order.setTotalAmount(total);
        orderRepository.save(order);
    }
    
    private OrderResponse convertToOrderResponse(Order order) {
        return new OrderResponse(
            order.getId(),
            order.getCustomerName(),
            order.getCustomerEmail(),
            order.getTotalAmount(),
            order.getStatus(),
            order.getCreatedAt(),
            order.getUpdatedAt()
        );
    }
    
    private OrderItemResponse convertToOrderItemResponse(OrderItem item) {
        return new OrderItemResponse(
            item.getId(),
            item.getOrder() != null ? item.getOrder().getId() : null,
            item.getProductId(),
            item.getProductName(),
            item.getQuantity(),
            item.getUnitPrice(),
            item.getTotalPrice()
        );
    }
}
