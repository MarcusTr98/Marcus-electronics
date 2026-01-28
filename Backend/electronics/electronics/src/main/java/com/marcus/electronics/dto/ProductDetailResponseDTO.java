package com.marcus.electronics.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailResponseDTO {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String categoryName;

    // 1. Danh sách thông số kỹ thuật (RAM: 8GB, CPU: i5...)
    private List<AttributeDTO> attributes;

    // 2. Danh sách các biến thể (SKU) để frontend xử lý logic chọn màu
    private List<SkuDTO> skus;

    // --- Inner Classes (DTO con nằm bên trong) ---

    @Data
    @Builder
    public static class AttributeDTO {
        private String name;
        private String value;
    }

    @Data
    @Builder
    public static class SkuDTO {
        private String skuCode;
        private BigDecimal price;
        private Integer stock;
        private String imageUrl;
        // Map này quan trọng: VD {"Màu sắc": "Đỏ", "Bộ nhớ": "256GB"}
        private Map<String, String> options;
    }
}