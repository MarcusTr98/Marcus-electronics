package com.marcus.electronics.service;

import com.marcus.electronics.dto.ProductDetailResponseDTO;
import com.marcus.electronics.dto.ProductListResponseDTO;
import com.marcus.electronics.model.*;
import com.marcus.electronics.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

        // 1. Lấy danh sách sản phẩm (Trang chủ)
        public List<ProductListResponseDTO> getAllProducts() {
                List<Product> products = productRepository.findAll();

                return products.stream().map(p -> ProductListResponseDTO.builder()
                                .id(p.getId())
                                .name(p.getName())
                                .slug(p.getSlug())
                                .price(p.getBasePrice())
                                .thumbnailUrl(p.getThumbnailUrl())
                                .categoryName(p.getCategory().getName())
                                .build()).collect(Collectors.toList());
        }

        // 2. Lấy chi tiết 1 sản phẩm (Trang Detail) - LOGIC PHỨC TẠP
        public ProductDetailResponseDTO getProductBySlug(String slug) {
                // B1: Tìm Product gốc
                Product product = productRepository.findBySlug(slug)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

                // B2: Lấy thông số kỹ thuật (EAV)
                List<ProductAttributeValue> attrValues = productAttrRepo.findByProductId(product.getId());
                List<ProductDetailResponseDTO.AttributeDTO> attrDTOs = attrValues.stream()
                                .map(av -> ProductDetailResponseDTO.AttributeDTO.builder()
                                                .name(av.getAttribute().getName()) // Tên thông số (RAM)
                                                .value(av.getValue()) // Giá trị (8GB)
                                                .build())
                                .collect(Collectors.toList());

                // B3: Lấy danh sách SKU (Biến thể)
                List<Sku> skus = skuRepository.findByProductId(product.getId());
                List<ProductDetailResponseDTO.SkuDTO> skuDTOs = new ArrayList<>();

                for (Sku sku : skus) {
                        // Với mỗi SKU, phải tìm xem nó gồm những Option nào (Màu gì, Size gì)
                        // Lưu ý: Ở model Sku, tôi chưa mapping SkuValue nên chỗ này tạm thời để trống
                        // Map options
                        // Thực tế bạn cần query thêm bảng SkuValue ở đây.

                        // Map Options giả lập để bạn hình dung
                        Map<String, String> optionsMap = new HashMap<>();
                        optionsMap.put("Mã kho", sku.getSkuCode()); // Tạm thời

                        skuDTOs.add(ProductDetailResponseDTO.SkuDTO.builder()
                                        .skuCode(sku.getSkuCode())
                                        .price(sku.getPrice())
                                        .stock(sku.getStock())
                                        .imageUrl(sku.getImageUrl())
                                        .options(optionsMap)
                                        .build());
                }

                // B4: Gộp lại trả về
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
}