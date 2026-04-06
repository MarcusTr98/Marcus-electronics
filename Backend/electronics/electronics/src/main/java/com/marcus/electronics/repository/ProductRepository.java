package com.marcus.electronics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import com.marcus.electronics.model.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ĐÃ FIX: Ép Hibernate kéo luôn Category lên, chống lỗi LazyInit 500
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.slug = :slug")
    Optional<Product> findBySlug(String slug);

    boolean existsBySlug(String slug);
    boolean existsByName(String name);

    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.isActive = true")
    List<Product> findAllActiveWithCategory();

    List<Product> findByIsActiveTrueOrderByIdDesc(Pageable pageable);
}