package com.marcus.electronics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marcus.electronics.model.Product;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Tìm sản phẩm hiển thị chi tiết
    Optional<Product> findBySlug(String slug);

    // Check trùng slug khi tạo mới
    boolean existsBySlug(String slug);

    boolean existsByName(String name);
}