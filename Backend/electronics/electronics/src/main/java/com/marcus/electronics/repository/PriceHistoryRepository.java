package com.marcus.electronics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marcus.electronics.model.PriceHistory;

import java.util.List;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {
    // Xem lịch sử giá của 1 SKU cụ thể
    List<PriceHistory> findBySkuIdOrderByUpdatedAtDesc(Long skuId);
}