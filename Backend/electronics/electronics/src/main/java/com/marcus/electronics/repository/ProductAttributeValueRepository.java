package com.marcus.electronics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marcus.electronics.model.ProductAttributeValue;

import java.util.List;

@Repository
public interface ProductAttributeValueRepository extends JpaRepository<ProductAttributeValue, Long> {
    // Lấy tất cả thông số của 1 sản phẩm để hiển thị bảng cấu hình
    List<ProductAttributeValue> findByProductId(Long productId);
}