package com.marcus.electronics.service;

import com.marcus.electronics.dto.CategoryDTO;
import com.marcus.electronics.exception.DataNotFoundException;
import com.marcus.electronics.model.Category;
import com.marcus.electronics.repository.CategoryRepository;
import com.marcus.electronics.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAllByIsActiveTrue()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public CategoryDTO getCategoryById(Integer id) throws DataNotFoundException {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy danh mục ID: " + id));

        if (!category.isActive()) {
            throw new DataNotFoundException("Danh mục ID: " + id + " đã bị vô hiệu hóa");
        }

        return toDTO(category);
    }

    @Transactional
    public CategoryDTO createCategory(CategoryDTO dto) throws DataNotFoundException {
        String slug = buildUniqueSlug(dto.getName());
        Category parent = resolveParent(dto.getParentId());

        Category category = Category.builder()
                .name(dto.getName())
                .slug(slug)
                .parent(parent)
                .isActive(true)
                .build();

        return toDTO(categoryRepository.save(category));
    }

    @Transactional
    public CategoryDTO updateCategory(Integer id, CategoryDTO dto) throws DataNotFoundException {
        Category category = findActiveById(id);

        category.setName(dto.getName());
        category.setParent(resolveParentForUpdate(id, dto.getParentId()));

        return toDTO(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(Integer id) throws DataNotFoundException {
        Category category = findActiveById(id);

        if (!categoryRepository.findByParent_IdAndIsActiveTrue(id).isEmpty()) {
            throw new IllegalStateException("Phải xóa hết danh mục con trước!");
        }

        category.setIsActive(false);
        categoryRepository.save(category);
    }

    // ==================== PRIVATE HELPERS ====================

    private Category findActiveById(Integer id) throws DataNotFoundException {
        return categoryRepository.findById(id)
                .filter(Category::isActive)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy danh mục ID: " + id));
    }

    private String buildUniqueSlug(String name) {
        String slug = SlugUtils.generate(name);
        if (categoryRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }
        return slug;
    }

    private Category resolveParent(Integer parentId) throws DataNotFoundException {
        if (parentId == null)
            return null;
        return categoryRepository.findById(parentId)
                .orElseThrow(() -> new DataNotFoundException("Danh mục cha không tồn tại"));
    }

    private Category resolveParentForUpdate(Integer selfId, Integer parentId)
            throws DataNotFoundException {
        if (parentId == null)
            return null;
        if (selfId.equals(parentId)) {
            throw new IllegalArgumentException("Danh mục không thể tự nhận chính mình làm cha!");
        }
        return categoryRepository.findById(parentId)
                .orElseThrow(() -> new DataNotFoundException("Danh mục cha không tồn tại"));
    }

    private CategoryDTO toDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .build();
    }
}