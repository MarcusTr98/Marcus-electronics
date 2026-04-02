package com.marcus.electronics.service;

import com.marcus.electronics.dto.ProductDetailResponseDTO;
import com.marcus.electronics.dto.ProductListResponseDTO;
import com.marcus.electronics.dto.ProductRequestDTO;
import com.marcus.electronics.model.*;
import com.marcus.electronics.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

        private final ProductRepository productRepository;
        private final ProductAttributeValueRepository productAttrRepo;
        private final SkuRepository skuRepository;
        private final CategoryRepository categoryRepository;
        private final OptionValueRepository optionValueRepository1;

        // ==================== CLIENT ====================

        @Transactional(readOnly = true)
        public List<ProductListResponseDTO> getAllProducts() {
                return productRepository.findAllActiveWithCategory()
                                .stream()
                                .map(p -> ProductListResponseDTO.builder()
                                                .id(p.getId())
                                                .name(p.getName())
                                                .slug(p.getSlug())
                                                .thumbnailUrl(p.getThumbnailUrl())
                                                .categoryName(p.getCategory() != null ? p.getCategory().getName()
                                                                : "Chưa phân loại")
                                                .basePrice(p.getBasePrice())
                                                .active(p.getIsActive())
                                                .build())
                                .collect(Collectors.toList());
        }

        @Transactional(readOnly = true)
        public ProductDetailResponseDTO getProductBySlug(String slug) {
                Product product = productRepository.findBySlug(slug)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm: " + slug));

                // Thông số kỹ thuật (EAV)
                List<ProductAttributeValue> attrValues = productAttrRepo.findByProductId(product.getId());
                List<ProductDetailResponseDTO.AttributeDTO> attrDTOs = attrValues.stream()
                                .map(av -> ProductDetailResponseDTO.AttributeDTO.builder()
                                                .name(av.getAttribute().getName())
                                                .value(av.getValue())
                                                .build())
                                .collect(Collectors.toList());

                // SKUs
                List<Sku> skus = skuRepository.findByProductId(product.getId());
                List<ProductDetailResponseDTO.SkuDTO> skuDTOs = new ArrayList<>();

                for (Sku sku : skus) {
                        // Build options map từ skuValue → optionValue → option
                        Map<String, String> optionsMap = new HashMap<>();
                        if (sku.getSkuValues() != null) {
                                for (SkuValue sv : sku.getSkuValues()) {
                                        String optionName = sv.getOptionValue().getOption().getName();
                                        String optionVal = sv.getOptionValue().getValue();
                                        optionsMap.put(optionName, optionVal);
                                }
                        }

                        skuDTOs.add(ProductDetailResponseDTO.SkuDTO.builder()
                                        .skuCode(sku.getSkuCode())
                                        .price(sku.getPrice())
                                        .stock(sku.getStock())
                                        .imageUrl(sku.getImageUrl())
                                        .options(optionsMap)
                                        .build());
                }

                return ProductDetailResponseDTO.builder()
                                .id(product.getId())
                                .name(product.getName())
                                .slug(product.getSlug())
                                .description(product.getDescription())
                                .categoryName(product.getCategory().getName())
                                .attributes(attrDTOs)
                                .skus(skuDTOs)
                                .build();
        }

        // ==================== ADMIN ====================

        @Transactional(readOnly = true)
        public List<ProductListResponseDTO> getAllProductsForAdmin() {
                List<Product> products = productRepository.findAll();
                List<Sku> allSkus = skuRepository.findAll();

                // 1. Map Tồn kho tổng
                Map<Long, Integer> stockMap = allSkus.stream()
                                .collect(Collectors.groupingBy(
                                                sku -> sku.getProduct().getId(),
                                                Collectors.summingInt(
                                                                sku -> sku.getStock() != null ? sku.getStock() : 0)));

                // 2. Lấy toàn bộ OptionValue (Cần viết @Query "SELECT ov FROM OptionValue ov
                // JOIN FETCH ov.option" để tối ưu)
                List<OptionValue> allOptionValues = optionValueRepository1.findAll();

                // 3. LOGIC SẮC BÉN: Gom nhóm đa tầng -> Map<ProductID, Map<Tên_Thuộc_Tính,
                // Số_Lượng_Giá_Trị>>
                Map<Long, Map<String, Long>> variantSummaryMap = allOptionValues.stream()
                                .collect(Collectors.groupingBy(
                                                ov -> ov.getOption().getProduct().getId(), // Nhóm 1: Theo ID Sản phẩm
                                                Collectors.groupingBy(
                                                                ov -> ov.getOption().getName(), // Nhóm 2: Theo Tên
                                                                                                // Thuộc tính (Màu sắc)
                                                                Collectors.counting() // Đếm số lượng giá trị (Đỏ,
                                                                                      // Xanh...)
                                                )));

                // 4. Map vào DTO
                return products.stream()
                                .map(p -> ProductListResponseDTO.builder()
                                                .id(p.getId())
                                                .name(p.getName())
                                                .slug(p.getSlug())
                                                .thumbnailUrl(p.getThumbnailUrl())
                                                .categoryName(p.getCategory() != null ? p.getCategory().getName()
                                                                : "Chưa phân loại")
                                                .basePrice(p.getBasePrice())
                                                .active(p.getIsActive())
                                                .totalStock(stockMap.getOrDefault(p.getId(), 0))
                                                // Gắn Map Summary vào đây
                                                .variantSummary(variantSummaryMap.getOrDefault(p.getId(),
                                                                new java.util.HashMap<>()))
                                                .build())
                                .collect(Collectors.toList());
        }

        @Transactional(readOnly = true)
        public Product getProductById(long id) {
                return productRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));
        }

        @Transactional
        public void createProduct(ProductRequestDTO dto) {
                // 1. Kiểm tra Category có tồn tại không
                Category category = categoryRepository.findById(dto.getCategoryId())
                                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));

                // 2. Map dữ liệu từ DTO sang Entity
                Product product = Product.builder()
                                .name(dto.getName())
                                .slug(dto.getSlug())
                                .description(dto.getDescription())
                                .category(category)
                                .thumbnailUrl(dto.getThumbnailUrl())
                                .basePrice(dto.getBasePrice())
                                .weightG(dto.getWeightG())
                                .lengthCm(dto.getLengthCm())
                                .widthCm(dto.getWidthCm())
                                .heightCm(dto.getHeightCm())
                                .isActive(dto.getActive() != null ? dto.getActive() : true)
                                .build();

                productRepository.save(product);
        }

        @Transactional
        public void updateProduct(Long id, ProductRequestDTO dto) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

                Category category = categoryRepository.findById(dto.getCategoryId())
                                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));

                // Cập nhật thông tin
                product.setName(dto.getName());
                product.setSlug(dto.getSlug());
                product.setDescription(dto.getDescription());
                product.setCategory(category);
                product.setThumbnailUrl(dto.getThumbnailUrl());
                product.setBasePrice(dto.getBasePrice());
                product.setWeightG(dto.getWeightG());
                product.setLengthCm(dto.getLengthCm());
                product.setWidthCm(dto.getWidthCm());
                product.setHeightCm(dto.getHeightCm());

                if (dto.getActive() != null) {
                        product.setIsActive(dto.getActive());
                }

                productRepository.save(product);
        }

        @Transactional
        public void deleteProduct(Long id) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
                // Soft Delete
                product.setIsActive(false);
                productRepository.save(product);
        }

        @Transactional
        public void restoreProduct(Long id) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
                // Khôi phục
                product.setIsActive(true);
                productRepository.save(product);
        }
}