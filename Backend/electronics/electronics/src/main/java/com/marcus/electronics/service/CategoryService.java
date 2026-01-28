package com.marcus.electronics.service;

import com.marcus.electronics.dto.CategoryDTO;
import com.marcus.electronics.model.Category;
import com.marcus.electronics.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // Lấy tất cả danh mục
    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        // Chuyển Entity -> DTO
        return categories.stream().map(cat -> CategoryDTO.builder()
                .id(cat.getId())
                .name(cat.getName())
                .slug(cat.getSlug())
                .parentId(cat.getParent() != null ? cat.getParent().getId() : null)
                .build()).collect(Collectors.toList());
    }
}