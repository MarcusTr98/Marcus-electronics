package com.marcus.electronics.repository;

import com.marcus.electronics.model.SkuValue;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SkuValueRepository extends JpaRepository<SkuValue, Long> {
    // Lấy danh sách giá trị option của một SKU (VD: SKU_001 có [Màu: Đỏ, Ram: 8GB])
    List<SkuValue> findBySkuId(Long skuId);
}