package com.marcus.electronics.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductListResponseDTO {
    private Long id;
    private String name;
    private String slug;
    private String thumbnailUrl;
    private String categoryName;
    private BigDecimal basePrice;
    private Boolean active;

    private Integer totalStock;

    private Map<String, Long> variantSummary; // Ví dụ: {"Màu sắc": 4, "Dung lượng": 3}
}