package com.example.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductReportResponse {
    private Integer id;
    private Integer productId;
    private Integer totalSold;
    private BigDecimal revenue;
    private BigDecimal cost;
    private BigDecimal profit;
}
