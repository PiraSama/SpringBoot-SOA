package com.example.repository;

import com.example.entity.ProductReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductReportRepository extends JpaRepository<ProductReport, Integer> {
    List<ProductReport> findByOrderReportId(Integer orderReportId);
}
