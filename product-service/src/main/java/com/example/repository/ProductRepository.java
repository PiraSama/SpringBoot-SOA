package com.example.repository;

import com.example.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository để truy vấn dữ liệu sản phẩm từ database
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    
    /**
     * Tìm sản phẩm theo tên (tìm kiếm chứa)
     */
    List<Product> findByNameContainingIgnoreCase(String name);
    
    /**
     * Kiểm tra xem sản phẩm có tồn tại hay không
     */
    boolean existsById(Integer id);
}
