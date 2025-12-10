package com.example.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "product_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductReport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_report_id")
    @JsonBackReference
    private OrderReport orderReport;
    
    @Column(name = "product_id", nullable = false)
    private Integer productId;
    
    @Column(name = "total_sold", nullable = false)
    private Integer totalSold;
    
    @Column(name = "revenue", precision = 10, scale = 2)
    private BigDecimal revenue;
    
    @Column(name = "cost", precision = 10, scale = 2)
    private BigDecimal cost;
    
    @Column(name = "profit", precision = 10, scale = 2)
    private BigDecimal profit;
}
