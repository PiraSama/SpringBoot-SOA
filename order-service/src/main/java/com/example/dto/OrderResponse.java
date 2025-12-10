package com.example.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Integer id;
    private String customerName;
    private String customerEmail;
    private BigDecimal totalAmount;
    private String status;
    private Long created_at;
    private Long updated_at;
}
