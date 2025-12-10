package com.example.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderItemRequest {
    private Integer productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
}
