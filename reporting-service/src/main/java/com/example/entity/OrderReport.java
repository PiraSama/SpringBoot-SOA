package com.example.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "orders_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderReport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "order_id", nullable = false)
    private Integer orderId;
    
    @Column(name = "total_revenue", precision = 10, scale = 2)
    private BigDecimal totalRevenue;
    
    @Column(name = "total_cost", precision = 10, scale = 2)
    private BigDecimal totalCost;
    
    @Column(name = "total_profit", precision = 10, scale = 2)
    private BigDecimal totalProfit;
    
    @OneToMany(mappedBy = "orderReport", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ProductReport> productReports;
}
