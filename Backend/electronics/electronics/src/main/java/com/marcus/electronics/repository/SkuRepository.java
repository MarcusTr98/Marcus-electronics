package com.marcus.electronics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marcus.electronics.model.Sku;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkuRepository extends JpaRepository<Sku, Long> {
    // Tìm SKU theo mã kho (Quan trọng khi quét mã vạch hoặc check đơn)
    Optional<Sku> findBySkuCode(String skuCode);

    // Lấy danh sách biến thể của 1 sản phẩm
    List<Sku> findByProductId(Long productId);
}