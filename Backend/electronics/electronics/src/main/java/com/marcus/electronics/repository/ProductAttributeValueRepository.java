package com.marcus.electronics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.marcus.electronics.model.ProductAttributeValue;

import java.util.List;

@Repository
public interface ProductAttributeValueRepository extends JpaRepository<ProductAttributeValue, Long> {

    // ĐÃ FIX: JOIN FETCH với bảng attribute để lấy được tên thông số (RAM, Màn
    // hình...)
    @Query("SELECT pav FROM ProductAttributeValue pav JOIN FETCH pav.attribute WHERE pav.product.id = :productId")
    List<ProductAttributeValue> findByProductId(Long productId);
}