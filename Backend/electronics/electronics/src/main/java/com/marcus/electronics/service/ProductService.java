package com.marcus.electronics.service;

import com.marcus.electronics.dto.ProductDetailResponseDTO;
import com.marcus.electronics.dto.ProductListResponseDTO;
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
        private final OptionRepository optionRepository;

        // ==================== CLIENT ====================

        @Transactional(readOnly = true)
        public List<ProductListResponseDTO> getAllProducts() {
                return productRepository.findAllActiveWithCategory()
                                .stream()
                                .map(p -> ProductListResponseDTO.builder()
                                                .id(p.getId())
                                                .name(p.getName())
                                                .slug(p.getSlug())
                                                .price(p.getBasePrice())
                                                .thumbnailUrl(p.getThumbnailUrl())
                                                .categoryName(p.getCategory().getName())
                                                .build())
                                .collect(Collectors.toList());
        }

        @Transactional(readOnly = true)
        public ProductDetailResponseDTO getProductBySlug(String slug) {
                Product product = productRepository.findBySlugActive(slug)
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
                // Admin thấy cả sản phẩm inactive
                return productRepository.findAll()
                                .stream()
                                .map(p -> ProductListResponseDTO.builder()
                                                .id(p.getId())
                                                .name(p.getName())
                                                .slug(p.getSlug())
                                                .price(p.getBasePrice())
                                                .thumbnailUrl(p.getThumbnailUrl())
                                                .categoryName(p.getCategory() != null ? p.getCategory().getName() : "")
                                                .build())
                                .collect(Collectors.toList());
        }

        @Transactional(readOnly = true)
        public Product getProductById(long id) {
                return productRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));
        }
}