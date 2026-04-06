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
    private BigDecimal basePrice;
    private String thumbnailUrl;
    private List<AttributeDTO> attributes;
    private List<SkuDTO> skus;

    @Data
    @Builder
    public static class AttributeDTO {
        private String name;
        private String value;
    }

    @Data
    @Builder
    public static class SkuDTO {
        private Long id;
        private String skuCode;
        private BigDecimal price; // Đổi từ basePrice thành price cho rõ ràng
        private Integer stock;
        private String imageUrl;
        private Map<String, String> options;
    }
}