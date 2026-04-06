package com.marcus.electronics.repository;

import com.marcus.electronics.model.Sku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SkuRepository extends JpaRepository<Sku, Long> {

    // 1. Dành cho Client: Chỉ lấy SKU đang bán
    @Query("""
                SELECT DISTINCT s FROM Sku s
                LEFT JOIN FETCH s.skuValues sv
                LEFT JOIN FETCH sv.optionValue ov
                LEFT JOIN FETCH ov.option
                WHERE s.product.id = :productId AND s.isActive = true
            """)
    List<Sku> findByProductId(@Param("productId") Long productId);

    // 2. DÀNH CHO ADMIN: Lấy tất cả (kể cả đã khóa) của 1 sản phẩm để quản lý
    @Query("""
                SELECT DISTINCT s FROM Sku s
                LEFT JOIN FETCH s.skuValues sv
                LEFT JOIN FETCH sv.optionValue ov
                LEFT JOIN FETCH ov.option
                WHERE s.product.id = :productId
            """)
    List<Sku> findAllByProductIdForAdmin(@Param("productId") Long productId);

    // 3. DÀNH CHO INVENTORY: Lấy toàn bộ kho hàng
    @Query("""
                SELECT DISTINCT s FROM Sku s
                JOIN FETCH s.product p
                LEFT JOIN FETCH p.category c
                LEFT JOIN FETCH s.skuValues sv
                LEFT JOIN FETCH sv.optionValue ov
                LEFT JOIN FETCH ov.option
            """)
    List<Sku> findAllForInventory();
}