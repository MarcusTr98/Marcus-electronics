package com.marcus.electronics.repository;

import com.marcus.electronics.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    // Lấy tất cả danh mục active, kèm parent trong 1 query (tránh N+1)
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.parent WHERE c.isActive = true ORDER BY c.name")
    List<Category> findAllByIsActiveTrue();

    Optional<Category> findBySlugAndIsActiveTrue(String slug);

    List<Category> findByParent_IdAndIsActiveTrue(Integer parentId);

    boolean existsBySlug(String slug);

    List<Category> findByIsActiveTrue();

}