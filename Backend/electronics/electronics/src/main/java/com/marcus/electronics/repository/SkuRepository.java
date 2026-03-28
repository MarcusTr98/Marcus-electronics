package com.marcus.electronics.repository;

import com.marcus.electronics.model.Sku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkuRepository extends JpaRepository<Sku, Long> {

    // JOIN FETCH skuValues → optionValue → option trong 1 query
    @Query("""
                SELECT s FROM Sku s
                LEFT JOIN FETCH s.skuValues sv
                LEFT JOIN FETCH sv.optionValue ov
                LEFT JOIN FETCH ov.option
                WHERE s.product.id = :productId AND s.isActive = true
            """)
    List<Sku> findByProductId(Long productId);
}