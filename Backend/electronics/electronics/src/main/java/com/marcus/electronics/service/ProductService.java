package com.marcus.electronics.service;

import com.marcus.electronics.dto.*;
import com.marcus.electronics.model.*;
import com.marcus.electronics.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

        private final ProductRepository productRepository;
        private final ProductAttributeValueRepository productAttrRepo;
        private final SkuRepository skuRepository;
        private final CategoryRepository categoryRepository;
        private final OptionValueRepository optionValueRepository;

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
                Product product = productRepository.findBySlug(slug.trim())
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với slug: " + slug));

                // 1. Map Thông số kỹ thuật (EAV)
                List<ProductDetailResponseDTO.AttributeDTO> attrDTOs = productAttrRepo.findByProductId(product.getId())
                                .stream()
                                .filter(av -> av.getAttribute() != null)
                                .map(av -> ProductDetailResponseDTO.AttributeDTO.builder()
                                                .name(av.getAttribute().getName())
                                                .value(av.getValue())
                                                .build())
                                .collect(Collectors.toList());

                // 2. Map Danh sách SKU (Biến thể)
                List<Sku> skus = skuRepository.findByProductId(product.getId());
                List<ProductDetailResponseDTO.SkuDTO> skuDTOs = skus.stream().map(sku -> {
                        Map<String, String> optionsMap = new HashMap<>();
                        if (sku.getSkuValues() != null) {
                                sku.getSkuValues().forEach(sv -> {
                                        if (sv.getOptionValue() != null && sv.getOptionValue().getOption() != null) {
                                                optionsMap.put(sv.getOptionValue().getOption().getName(),
                                                                sv.getOptionValue().getValue());
                                        }
                                });
                        }
                        return ProductDetailResponseDTO.SkuDTO.builder()
                                        .id(sku.getId())
                                        .skuCode(sku.getSkuCode())
                                        .price(sku.getPrice()) // Map đúng vào trường price
                                        .stock(sku.getStock())
                                        .imageUrl(sku.getImageUrl())
                                        .options(optionsMap)
                                        .build();
                }).collect(Collectors.toList());

                // 3. Trả về DTO tổng hợp
                return ProductDetailResponseDTO.builder()
                                .id(product.getId())
                                .name(product.getName())
                                .slug(product.getSlug())
                                .description(product.getDescription())
                                .basePrice(product.getBasePrice())
                                .thumbnailUrl(product.getThumbnailUrl())
                                .categoryName(product.getCategory() != null ? product.getCategory().getName()
                                                : "Chưa phân loại")
                                .attributes(attrDTOs)
                                .skus(skuDTOs)
                                .build();
        }

        // ==================== ADMIN ====================

        @Transactional(readOnly = true)
        public List<ProductListResponseDTO> getAllProductsForAdmin() {
                List<Product> products = productRepository.findAll();
                List<Sku> allSkus = skuRepository.findAll();

                Map<Long, Integer> stockMap = allSkus.stream()
                                .collect(Collectors.groupingBy(
                                                sku -> sku.getProduct().getId(),
                                                Collectors.summingInt(
                                                                sku -> sku.getStock() != null ? sku.getStock() : 0)));

                // ĐÃ SỬA: Gọi đúng tên repository
                List<OptionValue> allOptionValues = optionValueRepository.findAll();

                Map<Long, Map<String, Long>> variantSummaryMap = allOptionValues.stream()
                                .collect(Collectors.groupingBy(
                                                ov -> ov.getOption().getProduct().getId(),
                                                Collectors.groupingBy(
                                                                ov -> ov.getOption().getName(),
                                                                Collectors.counting())));

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
                Category category = categoryRepository.findById(dto.getCategoryId())
                                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));

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
                product.setIsActive(false);
                productRepository.save(product);
        }

        @Transactional
        public void restoreProduct(Long id) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
                product.setIsActive(true);
                productRepository.save(product);
        }

        @Transactional(readOnly = true)
        public List<ProductListResponseDTO> getFeaturedProducts() {
                org.springframework.data.domain.Pageable topEight = org.springframework.data.domain.PageRequest.of(0,
                                8);
                List<Product> products = productRepository.findByIsActiveTrueOrderByIdDesc(topEight);

                return products.stream().map(p -> ProductListResponseDTO.builder()
                                .id(p.getId())
                                .name(p.getName())
                                .slug(p.getSlug())
                                .thumbnailUrl(p.getThumbnailUrl())
                                .basePrice(p.getBasePrice())
                                .categoryName(p.getCategory() != null ? p.getCategory().getName() : "Khác")
                                .build()).collect(java.util.stream.Collectors.toList());
        }
}