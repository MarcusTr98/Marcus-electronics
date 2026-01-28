package com.marcus.electronics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marcus.electronics.model.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    // Tìm danh mục theo slug (để hiển thị URL đẹp)
    Optional<Category> findBySlug(String slug);

    // Lấy danh mục cha (Menu cấp 1)
    List<Category> findByParentIsNull();
}