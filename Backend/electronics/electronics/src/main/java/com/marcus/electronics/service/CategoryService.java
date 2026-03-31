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

    // ==========================================
    // KHU VỰC 1: DÀNH CHO ADMIN (Thấy tất cả, Thao tác CRUD)
    // Phục vụ cho: AdminCategoryController
    // ==========================================

    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        // Admin lấy tất cả (kể cả active = false)
        return categoryRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional
    public CategoryDTO createCategory(CategoryDTO dto) throws DataNotFoundException {
        String slug = buildUniqueSlug(dto.getName());
        Category parent = resolveParent(dto.getParentId());

        Category category = Category.builder()
                .name(dto.getName())
                .slug(slug)
                .parent(parent)
                // Admin tạo mới có quyền chọn active hay không (nếu form có gửi lên)
                // Nếu dto.getActive() null, mặc định là true
                .isActive(dto.getActive() != null ? dto.getActive() : true)
                .build();

        return mapToDTO(categoryRepository.save(category));
    }

    @Transactional
    public CategoryDTO updateCategory(Integer id, CategoryDTO dto) throws DataNotFoundException {
        // Admin có thể sửa cả những danh mục đang ẩn, nên dùng findById thay vì
        // findActiveById
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy danh mục ID: " + id));

        category.setName(dto.getName());
        category.setParent(resolveParentForUpdate(id, dto.getParentId()));

        // Admin có quyền cập nhật trạng thái Ẩn/Hiện
        if (dto.getActive() != null) {
            category.setIsActive(dto.getActive());
        }

        return mapToDTO(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(Integer id) throws DataNotFoundException {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy danh mục ID: " + id));

        // Kiểm tra xem có danh mục con nào đang bám vào nó không
        if (!categoryRepository.findByParent_IdAndIsActiveTrue(id).isEmpty()) {
            throw new IllegalStateException("Phải xóa hết danh mục con trước!");
        }

        // Thực hiện Soft Delete (Chuyển trạng thái sang ẩn) thay vì xóa vật lý (DELETE
        // FROM)
        category.setIsActive(false);
        categoryRepository.save(category);
    }

    @Transactional
    public void restoreCategory(Integer id) throws DataNotFoundException {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy danh mục ID: " + id));

        // Bật lại công tắc hiển thị
        category.setIsActive(true);
        categoryRepository.save(category);
    }

    // ==========================================
    // KHU VỰC 2: DÀNH CHO CLIENT (Chỉ thấy hàng đang bán)
    // Phục vụ cho: CategoryController
    // ==========================================

    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllActiveCategories() {
        // Khách hàng chỉ thấy danh mục đang active = true
        // YÊU CẦU: Cậu cần mở CategoryRepository và thêm hàm: List<Category>
        // findByIsActiveTrue();
        return categoryRepository.findByIsActiveTrue().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Integer id) throws DataNotFoundException {
        // Khách hàng chỉ được lấy chi tiết nếu danh mục đó đang active
        return categoryRepository.findById(id)
                .filter(Category::getIsActive)
                .map(this::mapToDTO)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy hoặc danh mục đã bị ẩn. ID: " + id));
    }

    // ==========================================
    // KHU VỰC 3: PRIVATE HELPERS (Công cụ xử lý ngầm)
    // ==========================================

    private CategoryDTO mapToDTO(Category category) {
        CategoryDTO dto = CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .active(category.getIsActive())
                .build();

        if (category.getParent() != null) {
            dto.setParentId(category.getParent().getId());
            dto.setParentName(category.getParent().getName());
        }
        return dto;
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

    private Category resolveParentForUpdate(Integer selfId, Integer parentId) throws DataNotFoundException {
        if (parentId == null)
            return null;
        if (selfId.equals(parentId)) {
            throw new IllegalArgumentException("Lỗi Logic: Danh mục không thể tự nhận chính mình làm cha!");
        }
        return categoryRepository.findById(parentId)
                .orElseThrow(() -> new DataNotFoundException("Danh mục cha không tồn tại"));
    }
}